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
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.Location;
import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.Resources;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 * 
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

    // Check for name is classRoot...
    if (term != null && CompilerUtils.isBraceTerm(term, CLASS_ROOT)) {
      for (IAbstract stmt : CompilerUtils.unWrap(CompilerUtils.braceArg(term))) {
        if (CompilerUtils.isIsStatement(stmt) && Abstract.isIdentifier(CompilerUtils.isStmtPattern(stmt), ROOT)
            && CompilerUtils.isStmtValue(stmt) instanceof StringLiteral) {
          root = Abstract.getString(CompilerUtils.isStmtValue(stmt));
        } else if (CompilerUtils.isIsStatement(stmt)
            && Abstract.isIdentifier(CompilerUtils.isStmtPattern(stmt), PKG_FUN)
            && CompilerUtils.isStmtValue(stmt) instanceof StringLiteral) {
          pkgFunName = Abstract.getString(CompilerUtils.isStmtValue(stmt));
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
  public CodeTree parse(ResourceURI uri, InputStream stream, ErrorReport errors) throws ResourceException
  {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);
    int mark = errors.errorCount();
    String root = null;
    String pkgFunName = null;

    IAbstract term = null;
    try (Reader rdr = new InputStreamReader(stream)) {
      term = parser.parse(uri, rdr, null);
    } catch (IOException e) {
      errors.reportError("IO problem in accessing resource " + uri + ": " + e.getMessage(), Location.location(uri));
    }

    // Check for name is classRoot...
    if (term != null && CompilerUtils.isBraceTerm(term, CLASS_ROOT)) {
      for (IAbstract stmt : CompilerUtils.unWrap(CompilerUtils.braceArg(term))) {
        if (CompilerUtils.isIsStatement(stmt) && Abstract.isIdentifier(CompilerUtils.isStmtPattern(stmt), ROOT)
            && CompilerUtils.isStmtValue(stmt) instanceof StringLiteral) {
          root = Abstract.getString(CompilerUtils.isStmtValue(stmt));
        } else if (CompilerUtils.isIsStatement(stmt)
            && Abstract.isIdentifier(CompilerUtils.isStmtPattern(stmt), PKG_FUN)
            && CompilerUtils.isStmtValue(stmt) instanceof StringLiteral) {
          pkgFunName = Abstract.getString(CompilerUtils.isStmtValue(stmt));
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
