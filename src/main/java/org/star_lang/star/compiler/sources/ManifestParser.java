package org.star_lang.star.compiler.sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.CodeParser;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Dict;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Refresher;
import org.star_lang.star.compiler.type.TypeNameHandler;
import org.star_lang.star.compiler.type.TypeParser;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.TypeParser.RegularTypeName;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.type.ContractImplementation;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeAlias;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

import javax.annotation.Resource;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class ManifestParser implements CodeParser {
  public static final String MANIFEST = "manifest";
  public static final String EXTENSION = "manifest";

  /**
   * parse a manifest source file which looks like:
   * <p>
   * <pre>
   * name is manifest{
   *   type foo is ....
   *   type bar is alias of ...
   *   contract con over .... is ...
   *   ...
   *   pkgFunName has type ()=>package{...}
   * }
   * </pre>
   */
  @Override
  public CodeTree parse(ResourceURI uri, ErrorReport errors) {
    try (Reader rdr = Resources.getReader(uri)) {
      return parse(uri, rdr, errors);
    } catch (ResourceException | IOException e) {
      errors.reportError("problem in accessing resource " + uri + ":" + e.getMessage(), Location.location(uri));
    }
    return null;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException {
    try (InputStreamReader rdr = new InputStreamReader(input)) {
      return parse(uri, rdr, errors);
    } catch (IOException e) {
      throw new ResourceException("poblem with parsing " + uri, e);
    }
  }

  private CodeTree parse(ResourceURI uri, Reader rdr, ErrorReport errors) throws ResourceException {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);
    int mark = errors.errorCount();

    IAbstract term = parser.parse(uri, rdr, null);

    // Check for name is manifest...
    if (term != null && CompilerUtils.isIsForm(term)
            && CompilerUtils.isPackageIdentifier(CompilerUtils.isFormPattern(term))
            && CompilerUtils.isBraceTerm(CompilerUtils.isFormValue(term), MANIFEST)) {
      String manifestName = CompilerUtils.getPackageIdentifier(CompilerUtils.isFormPattern(term));
      List<ITypeDescription> types = new ArrayList<>();
      List<ITypeAlias> aliases = new ArrayList<>();
      Map<String, TypeContract> contracts = new HashMap<>();
      Map<String, Set<ContractImplementation>> implementations = new HashMap<>();
      Map<String, Pair<IAbstract, IType>> defaults = new HashMap<>();
      Map<String, IAbstract> integrities = new HashMap<>();
      List<ResourceURI> imports = new ArrayList<>();
      IType pkgType = null;
      String pkgName = null;
      String pkgHash = null;
      ResourceURI manifestURI = null;
      Dictionary cxt = Dict.baseDict();

      for (IAbstract stmt : CompilerUtils.unWrap(CompilerUtils.braceArg(CompilerUtils.isFormValue(term)))) {
        if (CompilerUtils.isTypeAlias(stmt)) {
          TypeAlias alias = TypeParser.parseTypeAlias(stmt, cxt.fork(), errors);
          aliases.add(alias);
        } else if (CompilerUtils.isTypeDefn(stmt)) {
          ITypeDescription desc = TypeParser.parseTypeDefinition(stmt, defaults, integrities, cxt.fork(), cxt, errors,
                  true);
          types.add(desc);
          cxt.defineType(desc);
        } else if (CompilerUtils.isContractStmt(stmt)) {
          TypeContract contract = TypeParser.parseTypeContract(stmt, cxt.fork(), errors, defaults, integrities);
          contracts.put(contract.getName(), contract);
        } else if (CompilerUtils.isImplementationDeclaration(stmt)) {
          int errMark = errors.errorCount();
          String implVarName = Abstract.getId(CompilerUtils.implementationDeclarationVar(stmt));
          final boolean isDefault;
          if (Abstract.isUnary(stmt, StandardNames.DEFAULT)) {
            isDefault = true;
            stmt = Abstract.unaryArg(stmt);
          } else
            isDefault = false;

          Dictionary iCxt = cxt.fork();
          LayeredMap<String, TypeVar> tVars = new LayeredHash<>();
          List<IType> required = new ArrayList<>();
          final IAbstract conTerm;
          TypeNameHandler varHandler = new RegularTypeName(iCxt, tVars, AccessMode.readWrite, true, errors);
          if (Abstract.isBinary(stmt, StandardNames.WHERE)) {
            for (IAbstract arg : CompilerUtils.unWrap(Abstract.binaryRhs(stmt), StandardNames.AND)) {
              required.add(TypeParser.parseContractType(arg, iCxt, errors, varHandler));
            }
            conTerm = Abstract.binaryLhs(stmt);
          } else
            conTerm = stmt;

          if (Abstract.isBinary(conTerm, StandardNames.IMPLEMENTS)) {
            IType conType = TypeParser.parseContractType(Abstract.binaryRhs(conTerm), iCxt, errors, varHandler);

            if (errors.noNewErrors(errMark)) {
              IType implType = Refresher.generalize(TypeUtils.overloadedType(required, conType), tVars);
              Variable implVar = new OverloadedVariable(stmt.getLoc(), implType, Freshen.generalizeType(conType),
                      implVarName);
              String conName = conType.typeLabel();
              ContractImplementation implementation = new ContractImplementation(conName, implVar, isDefault);
              Set<ContractImplementation> impls = implementations.get(conName);
              if (impls == null) {
                impls = new HashSet<>();
                implementations.put(conName, impls);
              }
              impls.add(implementation);
            }
          } else
            errors.reportError("invalid manifest statement: " + stmt, stmt.getLoc());
        } else if (CompilerUtils.isTypeAnnotation(stmt) && Abstract.isIdentifier(CompilerUtils.typeAnnotatedTerm(stmt))) {
          IAbstract tp = CompilerUtils.typeAnnotation(stmt);
          pkgName = Abstract.getId(CompilerUtils.typeAnnotatedTerm(stmt));

          pkgType = TypeParser.parseType(tp, cxt.fork(), errors, AccessMode.readWrite, true);

          if (!TypeUtils.isTypeInterface(pkgType))
            errors.reportError("expecting a package specification, not " + pkgType, term.getLoc());
        } else if (CompilerUtils.isIsForm(stmt) && Abstract.isIdentifier(CompilerUtils.isFormPattern(stmt), "uri")) {
          IAbstract manifestValue = CompilerUtils.isFormValue(stmt);
          if (manifestValue instanceof StringLiteral) {
            String uriSpec = Abstract.getString(manifestValue);

            try {
              manifestURI = ResourceURI.parseURI(uriSpec);
            } catch (ResourceException e) {
              errors.reportError("illegal uri spec: " + uriSpec, stmt.getLoc());
            }
          } else
            errors.reportError("illegal uri spec " + manifestValue, stmt.getLoc());
        } else if (CompilerUtils.isIsForm(stmt) && Abstract.isIdentifier(CompilerUtils.isFormPattern(stmt), Manifest.PKGHASH)) {
          IAbstract manifestValue = CompilerUtils.isFormValue(stmt);
          if (manifestValue instanceof StringLiteral) {
            pkgHash = Abstract.getString(manifestValue);
          } else
            errors.reportError("illegal package hash spec " + manifestValue, stmt.getLoc());
        } else if (CompilerUtils.isEquals(stmt) && CompilerUtils.isIdentifier(CompilerUtils.equalityLhs(stmt), Manifest.IMPORTS) &&
                CompilerUtils.isSquareSequenceTerm(CompilerUtils.equalityRhs(stmt))) {
          IAbstract importAst = CompilerUtils.squareContent(CompilerUtils.equalityRhs(stmt));
          for (IAbstract imp : CompilerUtils.unWrap(importAst, StandardNames.COMMA)) {
            if (Abstract.isString(imp)) {
              String res = Abstract.getString(imp);
              imports.add(ResourceURI.parseURI(res));
            }
          }
        } else
          errors.reportError("unknown statement type: " + stmt + " in manifest", stmt.getLoc());
      }

      if (errors.noNewErrors(mark))
        return new Manifest(manifestURI, manifestName, pkgHash, types, aliases, contracts, imports, pkgType, pkgName);
    } else
      errors.reportError("not a valid manifest", Location.location(uri));
    return null;

  }

  @Override
  public String getExtension() {
    return EXTENSION;
  }
}
