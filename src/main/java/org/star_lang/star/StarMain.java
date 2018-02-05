package org.star_lang.star;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryClassLoader;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CafeManifest;
import org.star_lang.star.compiler.cafe.compile.ClassRoot;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.sources.PackageGrapher;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.compiler.util.Instrument;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.ValueDisplay;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.URIBasedCatalog;

/**
 * Top-level driver for running Star programs
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
public class StarMain
{
  public static final String TARGET_DIR = ApplicationProperties.getProperty("TARGET", null);
  public static final boolean COMPILE = ApplicationProperties.getProperty("COMPILE", true);
  public static final boolean GO = ApplicationProperties.getProperty("GO", true);

  private static CodeRepository standardRepository;

  /**
   * Entry point to compiling a Star program with the intention of executing it. This version has an
   * explicit URI, catalog and arguments as IValues
   * 
   * @param repository
   *          where code is stored
   * @param uri
   *          to the source of the program
   * @param catalog
   *          optional catalog for dereferencing imported packages
   * @param args
   *          top-level arguments to the main program being executed. Must be string values
   * @throws LanguageException
   * @throws EvaluationException
   * @throws CatalogException
   * @throws ResourceException
   * @throws RepositoryException
   */
  public static void compileAndGo(CodeRepository repository, ResourceURI uri, Catalog catalog, IValue args[])
      throws EvaluationException, ResourceException, CatalogException, LanguageException, RepositoryException
  {
    ErrorReport report = new ErrorReport();

    if (COMPILE)
      StarMake.compile(repository, uri, catalog, report);

    if (report.isErrorFree()) {
      System.err.print(report);
      if (GO) {
        report.clear();
        run(repository, uri, args, report);
        System.err.print(report);
      }
    } else
      throw new LanguageException(report);
  }

  /**
   * Compile a Star program without running it
   * 
   * @param repository
   *          where code is stored
   * @param uri
   *          to the source of the program
   * @param catalog
   *          optional catalog for dereferencing imported packages
   * @throws LanguageException
   */
  public static void compile(CodeRepository repository, ResourceURI uri, Catalog catalog) throws LanguageException
  {
    ErrorReport report = new ErrorReport();
    Intrinsics.intrinsics();

    StarMake.compile(repository, uri, catalog, report);

    if (report.isErrorFree())
      System.err.print(report);
    else
      throw new LanguageException(report);
  }

  /**
   * Invoke a Star function based on a package uri and program NAME
   * 
   * @param repository
   *          where code is stored
   * @param uri
   *          to the source of the program
   * @param name
   *          NAME of the function to invoke
   * @param args
   *          top-level arguments to the main program being executed
   * @throws LanguageException
   * @throws EvaluationException
   * @throws CatalogException
   * @throws ResourceException
   * @throws RepositoryException
   */
  public static IValue invoke(CodeRepository repository, ResourceURI uri, String name, IValue args[])
      throws EvaluationException, ResourceException, CatalogException, LanguageException, RepositoryException
  {
    ErrorReport errors = new ErrorReport();
    Intrinsics.intrinsics(); // This is hack to ensure that things are initialized in the right
    // order

    IValue pkg = ldPackage(repository, uri, errors);

    if (pkg != null && pkg instanceof IRecord) {
      IValue mn = ((IRecord) pkg).getMember(name);
      if (mn instanceof IFunction)
        return ((IFunction) mn).enter(args);

      if (!errors.isErrorFree())
        throw new LanguageException(errors);
    }
    return null;
  }

  public static IValue invoke(CodeRepository repository, ResourceURI uri, String name, IValue args[], ErrorReport errors)
      throws EvaluationException, ResourceException, CatalogException, LanguageException, RepositoryException
  {
    // Intrinsics.intrinsics(); // This is hack to ensure that things are initialized in the right
    // order

    RepositoryClassLoader loader = repository.classLoader();
    int alreadyLoaded = loader.getClassesLoaded();
    IValue pkg = ldPackage(repository, uri, errors);
    errors.addToCount("classes loaded", (loader.getClassesLoaded() - alreadyLoaded));

    if (pkg != null && pkg instanceof IRecord) {

      IValue mn = ((IRecord) pkg).getMember(name);
      if (mn instanceof IFunction)
        return ((IFunction) mn).enter(args);

      if (!errors.isErrorFree())
        throw new LanguageException(errors);
    }
    return null;
  }

  /**
   * Entry point to running a Star program with the intention of executing it. This version has an
   * explicit URI, catalog and arguments as IValues
   * 
   * @param repository
   *          where code is stored
   * @param uri
   *          to the source of the program
   * @param args
   *          top-level arguments to the main program being executed
   * @return the reporting structure
   * @throws LanguageException
   * @throws EvaluationException
   * @throws CatalogException
   * @throws ResourceException
   * @throws RepositoryException
   */
  public static ErrorReport run(CodeRepository repository, ResourceURI uri, IValue args[]) throws EvaluationException,
      ResourceException, CatalogException, RepositoryException, LanguageException
  {
    ErrorReport report = new ErrorReport();
    run(repository, uri, args, report);
    return report;
  }

  /**
   * Entry point to running a Star program with the intention of executing it. This version has an
   * explicit URI, catalog and arguments as IValues
   * 
   * @param repository
   *          where code is stored
   * @param uri
   *          to the source of the program
   * @param args
   *          top-level arguments to the main program being executed
   * @param errors
   *          error reporting structure
   * @throws LanguageException
   * @throws EvaluationException
   * @throws CatalogException
   * @throws ResourceException
   * @throws RepositoryException
   */
  public static void run(CodeRepository repository, ResourceURI uri, IValue args[], ErrorReport errors)
      throws EvaluationException, ResourceException, CatalogException, RepositoryException, LanguageException
  {
    Intrinsics.intrinsics(); // This is hack to ensure that things are initialized in the right
    // order

    IValue pkg = ldPackage(repository, uri, errors);

    if (pkg instanceof IRecord)
      try {
        IValue mn = ((IRecord) pkg).getMember(StandardNames.UMAIN);
        if (mn instanceof IFunction) {
          IValue argList = Cons.list(args);
          long nanos = System.nanoTime();

          try {
            ((IFunction) mn).enter(argList);
          } finally {
            errors.reportInfo("execution took " + ((System.nanoTime() - nanos) / Instrument.NANOS_PER_SECOND));
          }
        } else if (mn != null)
          errors.reportError("bad _main program:" + ValueDisplay.display(mn) + " in " + uri);
      } catch (IllegalArgumentException e) {
        if (e.getMessage().contains("_main not present"))
          errors.reportWarning(StringUtils.msg(e.getMessage(), " in ", uri));
        else
          throw e;
      }
    if (!errors.isErrorFree())
      throw new LanguageException(errors);
  }

  public static IValue loadPackage(CodeRepository repository, ResourceURI uri, ErrorReport errors,
      RepositoryClassLoader loader, IValue... args) throws EvaluationException, ResourceException, CatalogException,
      RepositoryException
  {
    Location loc = Location.location(uri);

    ClassRoot root = RepositoryManager.locateClassRoot(repository, loc, uri, errors);

    if (root != null) {
      Manifest manifest = RepositoryManager.locateStarManifest(repository, uri);
      String pkgClassName = root.getClassRoot() + "." + Utils.javaIdentifierOf(manifest.getPkgFunName());

      try {
        Class<?> main = loader.loadClass(pkgClassName);
        Constructor<?> constructor = main.getConstructor();
        IFunction pkgFun = (IFunction) constructor.newInstance();
        return pkgFun.enter(args);
      } catch (Exception e) {
        e.printStackTrace();
        errors.reportError(e.getClass().getSimpleName() + ":" + e.getMessage(), loc);
      }
    }
    return null;
  }

  public static IValue ldPackage(CodeRepository repository, ResourceURI uri, ErrorReport errors)
      throws EvaluationException, ResourceException, CatalogException, RepositoryException
  {
    Location loc = Location.location(uri);

    ClassRoot root = RepositoryManager.locateClassRoot(repository, loc, uri, errors);

    if (root != null) {
      String pkgClassName = root.getClassRoot() + "." + Names.PKG;

      try {
        Class<?> main = repository.classLoader().loadClass(pkgClassName);

        Field codeField = main.getField(Utils.javaIdentifierOf(root.getPkgFunName()));
        return (IValue) codeField.get(null);
      } catch (Exception e) {
        e.printStackTrace();
        errors.reportError(e.getClass().getSimpleName() + ":" + e.getMessage(), loc);
      }
    }
    return null;
  }

  public static void locateDependencies(CodeRepository repository, CodeCatalog products, ResourceURI uri,
      ErrorReport errors, Location loc) throws CatalogException, ResourceException, RepositoryException
  {
    CafeManifest manifest = RepositoryManager.locateCafeManifest(repository, uri);

    if (manifest != null) {
      for (ResourceURI imp : manifest.getImports()) {
        CodeCatalog impPkg = RepositoryManager.locatePackage(repository, imp);

        if (impPkg == null)
          errors.reportError("cannot locate imported package: " + imp, loc);
        else {
          products.mergeEntries(impPkg);
          locateDependencies(repository, products, imp, errors, loc);
        }
      }
    }
  }

  public static void main(String args[])
  {
    if (args.length > 0) {
      String source = args[0];
      try {
        String nArgs[] = new String[args.length - 1];
        for (int ix = 0; ix < nArgs.length; ix++)
          nArgs[ix] = args[ix + 1];

        ResourceURI uri = ResourceURI.parseURI(source);
        ResourceURI sourceURI = ApplicationProperties.wdURI.resolve(uri);

        Catalog catalog = PackageGrapher.lookForCatalog(sourceURI, new URIBasedCatalog(sourceURI, StarRules
            .starCatalog()), standardRepository());

        sourceURI = URIUtils.setKeyword(sourceURI, StandardNames.VERSION, catalog.getVersion());

        IValue pkgArgs[] = new IValue[nArgs.length];
        for (int ix = 0; ix < nArgs.length; ix++)
          pkgArgs[ix] = Factory.newString(nArgs[ix]);

        compileAndGo(standardRepository(), sourceURI, catalog, pkgArgs);
      } catch (LanguageException e) {
        System.err.println(e.getMessages().toString());
      } catch (EvaluationException e) {
        System.err.println("Run-time error in " + args[0] + "\n" + e.getMessage() + " at " + e.getLoc());
        e.printStackTrace();
      } catch (ResourceException e) {
        System.err.println(source + " cannot be parsed as a uri");
      } catch (CatalogException | RepositoryException e) {
        System.err.println(source + " not accessible");
      }
    } else
      System.err.println("usage: <> sourceFile");
  }

  public synchronized static CodeRepository standardRepository()
  {
    if (standardRepository == null)
      standardRepository = RepositoryManager.setupStandardRepository(Thread.currentThread().getContextClassLoader());
    return standardRepository;
  }
}
