package org.star_lang.star;

import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.*;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.DisplayQuoted;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.cafe.CafeDisplay;
import org.star_lang.star.compiler.cafe.compile.CompileCafe;
import org.star_lang.star.compiler.canonical.PackageTerm;
import org.star_lang.star.compiler.canonical.TypeDefinition;
import org.star_lang.star.compiler.format.rules.FmtCompile;
import org.star_lang.star.compiler.format.rules.FmtProgram;
import org.star_lang.star.compiler.generate.GenerateCafe;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.grammar.TermListener;
import org.star_lang.star.compiler.macrocompile.MacroCompiler;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.sources.Pkg;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeChecker;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.wff.WffEngine;
import org.star_lang.star.compiler.wff.WffProgram;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

public class CompileDriver {
  /**
   * Driver to compile a single source
   *
   * @param repository to store compiler products
   * @param uri        of the source to compile
   * @param srcText    the source text itself
   * @param catalog    current catalog to resolve imports
   * @param hash       used in repository
   * @param errors     error reporter
   * @return the actual uri of the source
   * @throws ResourceException
   * @throws CatalogException
   * @throws RepositoryException
   */
  public static ResourceURI compilePackage(CodeRepository repository, ResourceURI uri, String srcText, Catalog catalog,
                                           String hash, ErrorReport errors) throws ResourceException, CatalogException, RepositoryException {
    int errCount = errors.errorCount();
    boolean isPreamble = uri.getScheme().equals(Resources.STDSCHEME);
    boolean preludeOverride = !isPreamble || StarCompiler.SHOW_PRELUDE;

    String pkgName = URIUtils.getPackageName(uri);

    Location loc = Location.location(uri);

    Pkg pkg = new Pkg(uri, catalog, null, repository, errors);

    CodeCatalog bldCatalog = new CodeMemory(pkgName);
    Operators operators = Operators.operatorRoot().copy();
    WffProgram wffRules = new WffProgram();
    FmtProgram fmtRules = new FmtProgram();

    if (!isPreamble) {
      MetaRules meta = RepositoryManager.locateMeta(repository, StarCompiler.starRulesURI);
      assert meta != null;

      operators.importOperators(meta.getOperators(), errors, loc);
      wffRules.importRules(meta.getWffRules());
      fmtRules.importRules(meta.getFmtRules());
      errors.recordTime("import meta");
    }
    WffEngine validator = new WffEngine(errors, wffRules);
    OpGrammar parser = new OpGrammar(operators, errors);
    parser.addListener(new ImportOperators(repository, catalog, operators, errors));
    IAbstract term = null;
    try {
      try (Reader rdr = new StringReader(srcText)) {
        term = parser.parse(uri, rdr, null);
        errors.recordTime("parse");
      }
    } catch (IOException e) {
      errors.reportError("IO problem in accessing resource " + uri + ": " + e.getMessage(), loc);
    }

    if (errors.noNewErrors(errCount)) {
      Operators pkgOps = new Operators();
      WffProgram pkgWff = new WffProgram();
      FmtProgram pkgFmt = new FmtProgram();

      List<IAbstract> macroStmts = new ArrayList<>();
      List<IAbstract> normalStmts = new ArrayList<>();
      List<ResourceURI> imports = new ArrayList<>();

      errors.startTimer("find meta");
      findMetaRules(repository, term, errors, pkgOps, pkgWff, pkgFmt, catalog, macroStmts, normalStmts, imports,
              Visibility.pUblic);
      errors.recordTime("find meta");

      if (!isPreamble) {
        IAbstract starImport = CompilerUtils.privateStmt(loc, CompilerUtils.importStmt(loc, new StringLiteral(loc,
                StarCompiler.starRulesURI.toString())));
        normalStmts.add(0, starImport);
        macroStmts.add(0, starImport);
      }
      if (!normalStmts.isEmpty())
        term = CompilerUtils.tupleUp(loc, StandardNames.TERM, normalStmts);
      else
        term = Abstract.name(loc, StandardNames.BRACES);

      final String macrolabel = pkgName + MacroCompiler.MACRO_QUERY;

      int mark = errors.errorCount();
      errors.startTimer("generate macro");
      ResourceURI macroUri = MacroCompiler.macroUri(uri);
      IAbstract macro = MacroCompiler.compileMacroRules(loc, macroStmts, pkgName + MacroCompiler.MACRO_QUERY, catalog,
              repository, errors);
      errors.recordTime("generate macro");

      if (errors.noNewErrors(mark) && macro != null) {
        if (StarCompiler.SHOWMACROCODE && StarCompiler.SHOWMACRO && preludeOverride)
          System.out.println(DisplayQuoted.display(macro));

        Pkg macroPk = new Pkg(macroUri, catalog, null, repository, errors);

        errors.startTimer("macro type check");
        PackageTerm macroPkg = TypeChecker.typeOfPkg(macro, macroPk, errors);
        errors.recordTime("macro type check");

        if (StarCompiler.SHOWMACROCODE && StarCompiler.SHOWCANON && preludeOverride)
          System.out.println("canonical of macro code is " + macroPkg);

        if (errors.noNewErrors(mark)) {
          errors.startTimer("macro cafe gen");
          IArray macroContent = GenerateCafe.generatePackage(macroPkg, errors);
          errors.recordTime("macro cafe gen");

          if (StarCompiler.SHOWMACROCODE && StarCompiler.SHOWCAFE && preludeOverride) {
            System.out.println("Cafe of macro-code " + pkgName + " at " + uri + " is ");
            System.out.println(CafeDisplay.display(macroContent));
          }

          errors.startTimer("macro code gen");
          CodeCatalog macroCatalog = new CodeMemory(macrolabel);
          CompileCafe.compileContent(macroUri, repository, URIUtils.rootPath(macroUri), macroPkg.getPkgName(), macro
                  .getLoc(), macroContent, macroCatalog, errors);
          errors.recordTime("macro code gen");

          try {
            List<ITypeDescription> types = new ArrayList<>();
            List<ITypeAlias> aliases = new ArrayList<>();
            Map<String, TypeContract> contracts = new HashMap<>();

            // Extract the types that have been defined in this package
            for (TypeDefinition desc : macroPkg.getTypes())
              types.add(desc.getTypeDescription());

            for (ITypeAlias alias : macroPkg.getAliases())
              aliases.add(alias);
            for (TypeContract contract : macroPkg.getContracts())
              contracts.put(contract.getName(), contract);

            macroCatalog.addCodeEntry(StandardNames.MANIFEST, new Manifest(macroUri, macrolabel, hash, types, aliases,
                    contracts,macroPkg.getImports(), macroPkg.getPkgType(), macroPkg.getPkgName()));

            if (errors.isErrorFree()) {
              errors.startTimer("macro repository");
              repository.addRepositoryNode(macroUri, hash, macroCatalog);
              errors.recordTime("macro repository");
            }
          } catch (RepositoryException e) {
            errors.reportError(e.getMessage(), loc);
          }
        }
      }

      try {
        bldCatalog.addCodeEntry(StandardNames.METAENTRY, new MetaRules(uri, pkgName, imports, pkgOps, macrolabel,
                pkgWff, pkgFmt));
      } catch (RepositoryException e) {
        errors.reportError(e.getMessage(), loc);
      }
      operators.importOperators(pkgOps, errors, loc);
      wffRules.importRules(pkgWff);
      fmtRules.importRules(pkgFmt);

      if (!isPreamble) {
        validator.validate(term, StandardNames.WFF_STATEMENT);
        errors.recordTime("validation");
      }
      if (errors.isErrorFree()) {
        try {
          // This is a slight cheat, because the walker is the default replacer. But, the
          // top-level of a package is always 'interesting'
          errors.startTimer("run macros");
          term = (IAbstract) StarMain.invoke(repository, macroUri, macrolabel, new IValue[]{term}, errors);
          errors.recordTime("run macros");
          if (StarCompiler.TRACEMACRO)
            System.out.println("Macro replacement is " + term);
        } catch (EvaluationException e) {
          errors.reportError(e.getMessage(), e.getLoc());
        } catch (LanguageException e) {
          errors.mergeReport(e.getMessages());
        }

        if (StarCompiler.SHOWMACRO && preludeOverride)
          System.out.println(term);
      }
    }

    if (errors.isErrorFree()) {
      errors.startTimer("type check");
      PackageTerm pkgTerm = TypeChecker.typeOfPkg(term, pkg, errors);
      errors.recordTime("type check");
      if (pkgTerm.getUri() != null) {
        uri = pkgTerm.getUri();
      }

      if (StarCompiler.SHOWCANON && preludeOverride)
        System.out.println(pkgTerm.toString());
      if (errors.isErrorFree()) {

        IArray content = GenerateCafe.generatePackage(pkgTerm, errors);

        if (StarCompiler.SHOWCAFE && preludeOverride) {
          System.out.println("Cafe of " + pkgName + " at " + uri + " is ");
          System.out.println(CafeDisplay.display(content));
        }

        errors.recordTime("cafe generate");

        if (errors.isErrorFree()) {
          String rootPath = URIUtils.rootPath(uri);
          CompileCafe.compileContent(uri, repository, rootPath, pkgTerm.getPkgName(), pkgTerm.getLoc(), content,
                  bldCatalog, errors);

          errors.recordTime("compile");

          List<ITypeDescription> types = new ArrayList<>();
          List<ITypeAlias> aliases = new ArrayList<>();
          Map<String, TypeContract> contracts = new HashMap<>();

          // Extract the types that have been defined in this package
          for (TypeDefinition desc : pkgTerm.getTypes())
            types.add(desc.getTypeDescription());

          for (ITypeAlias alias : pkgTerm.getAliases())
            aliases.add(alias);
          for (TypeContract contract : pkgTerm.getContracts())
            contracts.put(contract.getName(), contract);

          bldCatalog.addCodeEntry(StandardNames.MANIFEST, new Manifest(uri, pkgTerm.getName(), hash, types, aliases,
                  contracts, pkgTerm.getImports(), pkgTerm.getPkgType(), pkgTerm.getPkgName()));

          if (errors.isErrorFree()) {
            try {
              errors.startTimer("repository");
              repository.addRepositoryNode(uri, hash, bldCatalog);
              errors.recordTime("repository");
            } catch (RepositoryException e) {
              errors.reportError(e.getMessage(), pkgTerm.getLoc());
            }
          }
        } else
          repository.removeRepositoryNode(uri);
      }
    }

    return uri;
  }

  private static class ImportOperators implements TermListener<IAbstract> {
    private final CodeRepository repository;
    private final Operators operators;
    private final Catalog catalog;
    private final ErrorReport errors;

    public ImportOperators(CodeRepository repository, Catalog catalog, Operators operators, ErrorReport errors) {
      this.operators = operators;
      this.errors = errors;
      this.repository = repository;
      this.catalog = catalog;
    }

    @Override
    public void processTerm(IAbstract term) {
      if (CompilerUtils.isPrivate(term))
        processTerm(CompilerUtils.privateTerm(term));
      else if (CompilerUtils.isImport(term)) {
        IAbstract pkgRef = CompilerUtils.importPkg(term);
        Location loc = term.getLoc();

        try {
          MetaRules imported = RepositoryManager.locateMeta(repository, CompileDriver.uriOfPkgRef(pkgRef, catalog));

          if (imported != null) {
            operators.importOperators(imported.getOperators(), errors, loc);
          } else
            errors.reportWarning("cannot process import of " + pkgRef, term.getLoc());
        } catch (IllegalArgumentException e) {
          errors.reportError("cannot process import of " + pkgRef + "\nsince '" + pkgRef
                  + "' is not a valid identifier for import.", loc);
        } catch (Exception e) {
          errors.reportWarning("cannot process import of " + pkgRef + "\nbecause " + e.getMessage(), term.getLoc());
        }
      }
    }
  }

  public static void validate(IAbstract term, ErrorReport errors, Location loc, String category,
                              CodeRepository repository) throws ResourceException, CatalogException, RepositoryException {
    MetaRules meta = RepositoryManager.locateMeta(repository, StarCompiler.starRulesURI);
    assert meta != null;

    WffEngine validator = new WffEngine(errors, meta.getWffRules());

    switch (validator.validate(term, category)) {
      case validates:
        break;
      case notApply:
        errors.reportWarning("cannot find validation for " + term + " as " + category, loc);
        break;
      case notValidates:
        errors.reportError(term + " does not validate as " + category, loc);
        break;
    }
  }

  private static void findMetaRules(CodeRepository repository, IAbstract term, ErrorReport errors, Operators operators,
                                    WffProgram wffRules, FmtProgram fmtRules, Catalog catalog, List<IAbstract> macroStmts,
                                    List<IAbstract> normalStmts, List<ResourceURI> imports, Visibility visibility) throws CatalogException,
          ResourceException {
    for (IAbstract stmt : CompilerUtils.unWrap(term)) {
      if (CompilerUtils.isPackageStmt(stmt) && CompilerUtils.packageContents(stmt) != null) {
        List<IAbstract> pkgContent = new ArrayList<>();
        findMetaRules(repository, CompilerUtils.packageContents(stmt), errors, operators, wffRules, fmtRules, catalog,
                macroStmts, pkgContent, imports, visibility);
        normalStmts.add(CompilerUtils.packageStmt(stmt.getLoc(), CompilerUtils.packageName(stmt), pkgContent));
      } else if (CompilerUtils.isPrivate(stmt))
        findMetaRules(repository, CompilerUtils.stripVisibility(stmt), errors, operators, wffRules, fmtRules, catalog,
            macroStmts, normalStmts, imports, Visibility.priVate);
      else if (CompilerUtils.isPublic(stmt))
        findMetaRules(repository, CompilerUtils.stripVisibility(stmt), errors, operators, wffRules, fmtRules, catalog,
                macroStmts, normalStmts, imports, Visibility.pUblic);
      else if (CompilerUtils.isImport(stmt)) {
        if (visibility == Visibility.priVate) {
          IAbstract prStmt = CompilerUtils.privateStmt(stmt.getLoc(), stmt);
          normalStmts.add(prStmt);
          macroStmts.add(prStmt);
        } else {
          normalStmts.add(stmt);
          macroStmts.add(stmt);
        }
        IAbstract pkgName = CompilerUtils.importPkg(stmt);
        Location loc = stmt.getLoc();

        ResourceURI pkgUri = uriOfPkgRef(pkgName, catalog);
        imports.add(pkgUri);

        MetaRules meta = RepositoryManager.locateMeta(repository, pkgUri);
        if (meta != null) {
          operators.importOperators(meta.getOperators(), errors, loc);
          wffRules.importRules(meta.getWffRules());
          fmtRules.importRules(meta.getFmtRules());
        } else
          errors.reportError("cannot import " + pkgName, loc);
      } else if (Abstract.isUnary(stmt, StandardNames.META_HASH)) {
        IAbstract rl = Abstract.unaryArg(stmt);
        if (Abstract.isBinary(rl, StandardNames.WFF_RULE) || Abstract.isBinary(rl, StandardNames.WFF_DEFINES))
          wffRules.defineValidationRule(rl, errors);
        else if (Abstract.isBinary(rl, StandardNames.MACRORULE) || Abstract.isBinary(rl, StandardNames.IS)) {
          macroStmts.add(stmt);
        } else if (Abstract.isBinary(rl, StandardNames.FMT_RULE))
          fmtRules.defineFormattingRule(FmtCompile.compileRule(rl, fmtRules, errors));
        else
          operators.declareOperator(errors, rl);
      } else if (visibility == Visibility.priVate)
        normalStmts.add(CompilerUtils.privateStmt(stmt.getLoc(), stmt));
      else if (visibility == Visibility.pUblic)
        normalStmts.add(stmt);
//      normalStmts.add(CompilerUtils.publicStmt(stmt.getLoc(), stmt));
      else if (CompilerUtils.isBraceTerm(stmt)) { // A special hack to allow for imports
        for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(stmt))) {
          if (CompilerUtils.isPrivate(el))
            el = CompilerUtils.privateTerm(el);
          if (CompilerUtils.isImport(el)) {
            IAbstract pkgName = CompilerUtils.importPkg(el);
            ResourceURI pkgUri = uriOfPkgRef(pkgName, catalog);
            imports.add(pkgUri);
            macroStmts.add(CompilerUtils.privateStmt(el.getLoc(), el));
          }
        }
        normalStmts.add(stmt);
      } else
        normalStmts.add(stmt);
    }
  }

  public static ResourceURI uriOfPkgRef(IAbstract pkg, Catalog catalog) throws ResourceException, CatalogException {
    if (Abstract.isIdentifier(pkg))
      return catalog.resolve(Abstract.getId(pkg));
    else if (pkg instanceof StringLiteral)
      return catalog.resolve(URIUtils.parseUri(Abstract.getString(pkg)));
    else if (Abstract.isParenTerm(pkg))
      return uriOfPkgRef(Abstract.deParen(pkg), catalog);
    else
      throw new ResourceException("invalid resource identifier: " + pkg);
  }
}
