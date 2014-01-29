package org.star_lang.star.compiler.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Stack;

import org.star_lang.star.CompileDriver;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CodeRepositoryImpl;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.format.rules.FmtCompile;
import org.star_lang.star.compiler.format.rules.FmtProgram;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.wff.WffEngine;
import org.star_lang.star.compiler.wff.WffProgram;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.Resources;
import com.starview.platform.resource.URIUtils;
import com.starview.platform.resource.catalog.Catalog;
import com.starview.platform.resource.catalog.CatalogException;
import com.starview.platform.resource.catalog.URIBasedCatalog;

/**
 * Implement source-code formatting of StarRules text based on rules
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
public class Formatter
{
  private final FormatRanges ranges;
  private final String original;
  int currColumn = 0;
  boolean onlySpaces = true; // have we emitted any non-spaces on this line?
  boolean inLineComment = false;
  boolean inBlockComment = false;
  final StringBuilder blder = new StringBuilder();
  final int oLength;

  public static final boolean TRACE = false;

  private Formatter(String original, FormatRanges ranges)
  {
    this.original = original;
    this.ranges = ranges;
    this.oLength = original.length();
  }

  public static String formatSource(ResourceURI uri, ErrorReport errors, String category, CodeRepository repository,
      Catalog catalog) throws LanguageException, ResourceException, CatalogException, RepositoryException
  {
    // reparse the original
    IAbstract parse = StarCompiler.parse(uri, errors, repository);

    if (errors.isErrorFree()) {
      WffProgram wffRules = new WffProgram();
      FmtProgram fmtRules = new FmtProgram();

      MetaRules meta = RepositoryManager.locateMeta(repository, StarCompiler.starRulesURI);
      assert meta != null;

      wffRules.importRules(meta.getWffRules());
      fmtRules.importRules(meta.getFmtRules());

      List<ResourceURI> imports = new ArrayList<>();
      findMetaRules(parse, catalog, repository, errors, wffRules, fmtRules, imports);

      WffEngine validator = new WffEngine(errors, wffRules);

      // Decorate tree with categories
      validator.validate(parse, StandardNames.WFF_STATEMENT);
      FormatRanges formats = new FormatRanges();

      // Apply rules
      IAbstractVisitor formatVisitor = new FormatVisitor(fmtRules, formats);
      parse.accept(formatVisitor);

      String original = Resources.getUriContent(uri);
      Formatter formatter = new Formatter(original, formats);
      return formatter.format(original);

    } else
      throw new LanguageException(errors);
  }

  private String format(String original)
  {
    FormatPolicy policies = new FormatPolicy(0, original.length());
    Stack<FormatPolicy> policyStack = new Stack<FormatPolicy>();
    int ix = 0;
    int oLength = original.length();

    while (ix < oLength) {
      SortedMap<Integer, Map<String, IAttribute>> starts = ranges.getStarts(ix);

      // Adjust policies based on entry to new region
      if (starts != null) {
        for (Entry<Integer, Map<String, IAttribute>> entry : starts.entrySet()) {
          policyStack.push(policies);
          policies = policies.adjustPolicies(ix, entry.getKey(), entry.getValue());
          if (policies.breakBefore)
            ix = breakLine(policies, ix);
          adjustIndent(policies);
        }
      }

      int curr = original.codePointAt(ix);
      ix = original.offsetByCodePoints(ix, 1);

      switch (curr) {
      case '\n':
        ix = breakLine(policies, ix);
        break;
      case '-': // Check for line comment
      {
        if (!inLineComment && !inBlockComment && policies.lineCommentColumnPolicy >= currColumn
            && (original.startsWith("- ", ix) || original.startsWith("-\t", ix))) {
          if (onlySpaces)
            adjustIndent(policies);
          else {
            int endPoint = blder.length() - 1;
            char ch = blder.charAt(endPoint);
            while (currColumn > 0 && ch == '\t' || ch == ' ') {
              blder.deleteCharAt(endPoint);
              endPoint--;
              ch = blder.charAt(endPoint);
              currColumn--;
            }

            while (currColumn < policies.lineCommentColumnPolicy) {
              blder.append(' ');
              currColumn++;
            }
          }
          inLineComment = true;
        }
        blder.appendCodePoint(curr);
        currColumn++;
        onlySpaces = false;
        break;
      }

      case '/': // Check for block comment
      {
        if (!inLineComment && !inBlockComment && policies.lineCommentColumnPolicy >= currColumn
            && original.startsWith("*", ix)) {
          inBlockComment = true;
        }
        blder.appendCodePoint(curr);
        currColumn++;
        onlySpaces = false;
        break;
      }

      case '*': // Check for end of block comment
        if (inBlockComment && ix < oLength && original.codePointAt(ix) == '/') {
          inBlockComment = false;
        }
        blder.appendCodePoint(curr);
        onlySpaces = false;
        currColumn++;
        break;

      default:
        blder.appendCodePoint(curr);
        if (!Character.isWhitespace(curr))
          onlySpaces = false;
        else if (inBlockComment && policies.commentWrap && currColumn >= policies.commentWrapColumn)
          ix = breakLine(policies, ix);
        currColumn++;
        break;
      }

      while (!policyStack.isEmpty() && policies.expiredLocation(ix)) {
        if (policies.breakAfter) {
          if (policies.breakAfterToken != null)
            ix = scanForToken(policies.breakAfterToken, ix);

          ix = breakLine(policies, ix);
        }

        policies = policyStack.pop();
        adjustIndent(policies);
      }
    }
    return blder.toString();
  }

  private void adjustIndent(FormatPolicy policies)
  {
    if (onlySpaces && currColumn != policies.indentPolicy) {
      if (currColumn > policies.indentPolicy) {
        while (currColumn > policies.indentPolicy && blder.charAt(blder.length() - 1) == ' ') {
          currColumn--;
          blder.setLength(blder.length() - 1);
        }
      } else {
        while (currColumn < policies.indentPolicy) {
          blder.append(' ');
          currColumn++;
        }
      }
    }
  }

  private int breakLine(FormatPolicy policies, int ix)
  {
    inLineComment = false;
    onlySpaces = true;

    if (policies.blankLinePolicy >= 0) {
      int blankLines = policies.blankLinePolicy;

      // Trim blank space already generated
      int endPoint = blder.length();
      while (endPoint > 0 && Character.isWhitespace(blder.charAt(endPoint - 1))) {
        endPoint--;
      }

      blder.setLength(endPoint);
      currColumn = 0;

      while (ix < oLength && Character.isWhitespace(original.codePointAt(ix)))
        ix = original.offsetByCodePoints(ix, 1);

      while (--blankLines >= 0)
        blder.append('\n');
    } else {
      // Consume extra new lines and white space
      while (ix < oLength && Character.isWhitespace(original.codePointAt(ix)))
        ix = original.offsetByCodePoints(ix, 1);
    }

    if (ix < oLength) {
      for (; currColumn < policies.indentPolicy; currColumn++)
        blder.append(' ');
    }

    return ix;
  }

  private int scanForToken(String look, int ix)
  {
    while (ix < oLength && Character.isWhitespace(original.codePointAt(ix)))
      ix = original.offsetByCodePoints(ix, 1);
    if (StringUtils.lookingAt(original, ix, look)) {
      blder.append(look);
      return ix + look.length();
    } else
      return ix;
  }

  private static void findMetaRules(IAbstract term, Catalog catalog, CodeRepository repository, ErrorReport errors,
      WffProgram wffRules, FmtProgram fmtRules, List<ResourceURI> imports) throws CatalogException, ResourceException
  {
    for (IAbstract stmt : CompilerUtils.unWrap(term)) {
      if (CompilerUtils.isPackageStmt(stmt) && CompilerUtils.packageContents(stmt) != null) {
        findMetaRules(CompilerUtils.packageContents(stmt), catalog, repository, errors, wffRules, fmtRules, imports);
      } else if (CompilerUtils.isPrivate(stmt))
        findMetaRules(CompilerUtils.privateTerm(stmt), catalog, repository, errors, wffRules, fmtRules, imports);
      else if (CompilerUtils.isImport(stmt)) {
        IAbstract pkgName = CompilerUtils.importPkg(stmt);
        ResourceURI pkgUri = CompileDriver.uriOfPkgRef(pkgName, catalog);
        imports.add(pkgUri);

        MetaRules meta = RepositoryManager.locateMeta(repository, pkgUri);
        if (meta != null) {
          wffRules.importRules(meta.getWffRules());
          fmtRules.importRules(meta.getFmtRules());
        }
      } else if (Abstract.isUnary(stmt, StandardNames.META_HASH)) {
        IAbstract rl = Abstract.unaryArg(stmt);
        if (Abstract.isBinary(rl, StandardNames.WFF_RULE) || Abstract.isBinary(rl, StandardNames.WFF_DEFINES))
          wffRules.defineValidationRule(rl, errors);
        else if (Abstract.isBinary(rl, StandardNames.FMT_RULE))
          fmtRules.defineFormattingRule(FmtCompile.compileRule(rl, fmtRules, errors));
      }
    }
  }

  public static ResourceURI uriOfPkgRef(IAbstract pkg, Catalog catalog) throws ResourceException, CatalogException
  {
    if (Abstract.isIdentifier(pkg))
      return catalog.resolve(Abstract.getId(pkg));
    else if (pkg instanceof StringLiteral)
      return catalog.resolve(URIUtils.parseUri(Abstract.getString(pkg)));
    else if (Abstract.isParenTerm(pkg))
      return uriOfPkgRef(Abstract.deParen(pkg), catalog);
    else
      throw new ResourceException("invalid resource identifier: " + pkg);
  }

  public static void main(String args[])
  {
    ErrorReport errors = new ErrorReport();
    if (args.length > 0) {
      try {
        String nArgs[] = new String[args.length - 1];
        for (int ix = 0; ix < nArgs.length; ix++)
          nArgs[ix] = args[ix + 1];

        CodeRepository repository = new CodeRepositoryImpl(Thread.currentThread().getContextClassLoader(), true, errors);

        ResourceURI uri = ResourceURI.parseURI(args[0]);
        ResourceURI sourceURI = ApplicationProperties.wdURI.resolve(uri);

        Catalog catalog = new URIBasedCatalog(sourceURI, StarRules.starCatalog());
        String result = formatSource(uri, errors, StandardNames.PACKAGE, repository, catalog);

        System.out.println(result);
      } catch (Exception e) {
        System.err.println("Format errors in " + args[0] + "\n" + e.getMessage());
      }
    } else
      System.err.println("usage: <> sourceFile");
  }
}
