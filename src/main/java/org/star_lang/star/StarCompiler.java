package org.star_lang.star;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeMemory;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.URIBasedCatalog;

/**
 * Master access class for compiling Star programs
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
public class StarCompiler
{
  public static final String STAR_PATH = ApplicationProperties.getProperty("STARPATH", null);
  public static final String STAR_DIR = ApplicationProperties.getProperty("STAR", ApplicationProperties.WD);
  public static final String VERSION = ApplicationProperties.getProperty("VERSION", null);

  public static final boolean SHOW_PRELUDE = ApplicationProperties.getProperty("SHOW_PRELUDE", false);

  public static final boolean SHOWCANON = ApplicationProperties.getProperty("SHOW_CANON", false);
  public static final boolean SHOWMACRO = ApplicationProperties.getProperty("SHOW_MACRO", false);
  public static final boolean SHOWMACROCODE = ApplicationProperties.getProperty("SHOW_MACRO_CODE", false);
  public static final boolean SHOWCAFE = ApplicationProperties.getProperty("SHOW_CAFE", false);
  public static final boolean SHOWBYTECODE = ApplicationProperties.getProperty("SHOW_BYTECODE", false);

  public static final boolean SHOWGRAPH = ApplicationProperties.getProperty("SHOW_GRAPH", false);

  public final static boolean TRACEMACRO = ApplicationProperties.getProperty("TRACE_MACRO", false);
  public static final boolean TEST_REGEXP = ApplicationProperties.getProperty("TEST_REGEXP", false);

  public final static boolean TRACE_MONASTICATION = ApplicationProperties.getProperty("TRACE_MONASTICATION", false);

  public static final boolean TEST_TVAR_DISPLAY = ApplicationProperties.getProperty("TVAR_DISPLAY", true);
  public static final boolean TVAR_DISPLAY_READONLY = ApplicationProperties.getProperty("TVAR_DISPLAY_READONLY", false);

  public static final boolean SHOWTIMING = ApplicationProperties.getProperty("SHOW_TIMING", false);

  public static final int MATCHDEPTH = ApplicationProperties.getProperty("MATCHDEPTH", 5);

  public static String standardExtensions[] = { ".srule", ".star", ".str" };

  public static final String starUri = "std:star.star";
  public static final ResourceURI starRulesURI = URIUtils.create(Resources.STDSCHEME, "star.star");

  static {
    String transducerRule = ApplicationProperties.getProperty("TRANSDUCER", null);
    if (transducerRule != null)
      try {
        URIUtils.setupUriTransducerRule(transducerRule);
      } catch (LanguageException e) {
        System.err.println(e.getMessage());
        System.exit(1);
      }
  }

  /**
   * Entry point to compiling a Star program. This version has an explicit URI, catalog
   * 
   * @param source
   *          to the source of the program
   * @param catalog
   *          optional catalog for dereferencing imported packages
   * @param repository
   *          code repository
   * @throws CatalogException
   * @throws ResourceException
   * @throws RepositoryException
   */
  public static void compile(ResourceURI source, Catalog catalog, CodeRepository repository) throws ResourceException,
      CatalogException, LanguageException, RepositoryException
  {
    ErrorReport errors = new ErrorReport();
    try {
      CompileDriver.compilePackage(repository, source, Resources.getUriContent(source), catalog, Resources
          .resourceHash(source), errors);
    } finally {
      if (!errors.isErrorFree())
        throw new LanguageException(errors);
    }
  }

  public static void main(String args[])
  {
    ErrorReport errors = new ErrorReport();
    if (args.length > 0) {
      String source = args[0];
      try {
        ResourceURI uri = ResourceURI.parseURI(source);
        ResourceURI sourceURI = ApplicationProperties.wdURI.resolve(uri);
        Catalog catalog = new URIBasedCatalog(sourceURI, StarRules.starCatalog());

        sourceURI = URIUtils.setKeyword(sourceURI, StandardNames.VERSION, catalog.getVersion());

        ClassLoader defltLoader = Thread.currentThread().getContextClassLoader();
        CodeRepository repository = RepositoryManager.setupStandardRepository(defltLoader);

        StarMake.compile(repository, sourceURI, catalog, errors);

        if (!errors.isWarningAndErrorFree())
          System.err.println(errors);
      } catch (ResourceException e) {
        System.err.println(source + " cannot be parsed as a uri");
      }
    } else
      System.err.println("usage: <> sourceFile");
  }

  public static IAbstract parse(ResourceURI uri, ErrorReport errors, CodeRepository repository)
  {
    Operators operators = Operators.operatorRoot().copy();
    Location loc = Location.location(uri);
    try {
      MetaRules meta = RepositoryManager.locateMeta(repository, starRulesURI);
      assert meta != null;

      operators.importOperators(meta.getOperators(), errors, loc);

      OpGrammar parser = new OpGrammar(operators, errors);

      try (Reader rdr = Resources.getReader(uri)) {
        return parser.parse(uri, rdr, loc);
      } catch (IOException e) {
        errors.reportError("resource exception in parsing " + uri + "\nbecause " + e.getMessage(), loc);
        return null;
      }
    } catch (ResourceException e) {
      errors.reportError("resource exception in parsing " + uri + "\nbecause " + e.getMessage(), loc);
      return null;
    }
  }

  public CodeCatalog compileTypes(CodeRepository repository, ResourceURI uri, ErrorReport errors)
  {
    Location loc = Location.location(uri);

    try {
      StarMake.compile(repository, uri, StarRules.starCatalog(), errors);

      CodeCatalog pkg = RepositoryManager.locatePackage(repository, uri);
      if (pkg != null && errors.isErrorFree()) {
        CodeCatalog pkgCode = (CodeCatalog) pkg.resolve(StandardNames.COMPILED, CodeCatalog.EXTENSION);
        CodeCatalog products = new CodeMemory(pkgCode);
        StarMain.locateDependencies(repository, products, uri, errors, loc);

        return products;
      }
    } catch (Exception e) {
      errors.reportError(e.getClass().getSimpleName() + ":" + e.getMessage(), loc);
    }

    return null;
  }

  public static IAbstract parseString(String text, Location loc, ErrorReport errors)
  {
    OpGrammar parser = new OpGrammar(Operators.operatorRoot(), errors);

    return parser.parse(loc.getUri(), new StringReader(text), loc);
  }

  public static IValue localCompile(CodeRepository repository, ResourceURI source, Catalog catalog, ErrorReport errors)
      throws EvaluationException, ResourceException, CatalogException, RepositoryException
  {
    StarMake.compile(repository, source, catalog, errors);

    if (errors.isErrorFree()) {
      return StarMain.ldPackage(repository, source, errors);
    } else
      return null;
  }
}
