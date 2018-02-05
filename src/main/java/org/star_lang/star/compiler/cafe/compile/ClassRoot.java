package org.star_lang.star.compiler.cafe.compile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import org.star_lang.star.code.repository.CodeParser;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;

/**
 *
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
@SuppressWarnings("serial")
public class ClassRoot implements CodeTree, CodeParser
{
  private static final String CLASS_ROOT = "classRoot";
  private static final String ROOT = "root";
  private static final String PKG_FUN = "packageFunction";
  public static final String EXTENSION = "root";

  private final String classRoot;
  private final String pkgFunName;

  public ClassRoot(String classRoot, String pkgFunName)
  {
    this.classRoot = classRoot;
    this.pkgFunName = pkgFunName;
  }

  public String getClassRoot()
  {
    return classRoot;
  }

  public String getPkgFunName()
  {
    return pkgFunName;
  }

  @Override
  public void write(File file) throws IOException
  {
    try (PrintStream print = new PrintStream(file)) {
      print.append(toString());
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(CLASS_ROOT);
    int mark = disp.markIndent(2);
    disp.append("{\n");
    disp.appendWord(ROOT);
    disp.appendWord(StandardNames.IS);
    disp.append(" ");
    disp.appendQuoted(classRoot);
    disp.append(";\n");
    disp.appendWord(PKG_FUN);
    disp.appendWord(StandardNames.IS);
    disp.append(" ");
    disp.appendQuoted(pkgFunName);
    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public CodeTree parse(ResourceURI uri, ErrorReport errors) throws ResourceException
  {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);
    int mark = errors.errorCount();
    String root = null;
    String pkgFunName = null;

    IAbstract term = null;
    try (Reader rdr = Resources.getReader(uri)) {
      term = parser.parse(uri, rdr, null);
    } catch (ResourceException e) {
      errors.reportError("problem in accessing resource " + uri + ":" + e.getMessage(), Location.location(uri));
    } catch (IOException e) {
      errors.reportError("IO problem in accessing resource " + uri + ": " + e.getMessage(), Location.location(uri));
    }

    // Check for NAME is classRoot...
    if (term != null && CompilerUtils.isBraceTerm(term, CLASS_ROOT)) {
      for (IAbstract stmt : CompilerUtils.unWrap(CompilerUtils.braceArg(term))) {
        if (CompilerUtils.isIsForm(stmt)) {
          IAbstract lhs = CompilerUtils.isFormPattern(stmt);
          IAbstract rhs = CompilerUtils.isFormValue(stmt);
          if (Abstract.isIdentifier(lhs, ROOT) && rhs instanceof StringLiteral) {
            root = Abstract.getString(rhs);
            continue;
          } else if (Abstract.isIdentifier(lhs, PKG_FUN) && rhs instanceof StringLiteral) {
            pkgFunName = Abstract.getString(rhs);
            continue;
          }
        }
        errors.reportError("unknown statement type: " + stmt + " in manifest", stmt.getLoc());
      }

      if (errors.noNewErrors(mark))
        return new ClassRoot(root, pkgFunName);
    } else
      errors.reportError("not a valid class root manifest", Location.location(uri));
    return null;
  }

  @Override
  public CodeTree parse(ResourceURI uri, InputStream input, ErrorReport errors) throws ResourceException
  {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);
    int mark = errors.errorCount();
    String root = null;
    String pkgFunName = null;

    IAbstract term = null;
    try (Reader rdr = new InputStreamReader(input)) {
      term = parser.parse(uri, rdr, null);
    } catch (IOException e) {
      errors.reportError("IO problem in accessing resource " + uri + ": " + e.getMessage(), Location.location(uri));
    }

    // Check for NAME is classRoot...
    if (term != null && CompilerUtils.isBraceTerm(term, CLASS_ROOT)) {
      for (IAbstract stmt : CompilerUtils.unWrap(CompilerUtils.braceArg(term))) {
        if (Abstract.isBinary(stmt, StandardNames.IS)) {
          IAbstract lhs = Abstract.binaryLhs(stmt);
          IAbstract rhs = Abstract.binaryRhs(stmt);

          if (Abstract.isIdentifier(lhs, ROOT) && rhs instanceof StringLiteral)
            root = Abstract.getString(rhs);
          else if (Abstract.isIdentifier(lhs, PKG_FUN) && rhs instanceof StringLiteral)
            pkgFunName = Abstract.getString(rhs);
          else
            errors.reportError("unknown statement type: " + stmt + " in manifest", stmt.getLoc());
        } else
          errors.reportError("unknown statement type: " + stmt + " in manifest", stmt.getLoc());
      }

      if (errors.noNewErrors(mark))
        return new ClassRoot(root, pkgFunName);
    } else
      errors.reportError("not a valid class root manifest", Location.location(uri));
    return null;
  }

  @Override
  public String getPath()
  {
    return classRoot;
  }

  @Override
  public String getExtension()
  {
    return EXTENSION;
  }
}
