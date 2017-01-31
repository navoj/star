package org.star_lang.star.code.repository;

import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarMain;
import org.star_lang.star.code.Manifest;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CafeManifest;
import org.star_lang.star.compiler.cafe.compile.ClassRoot;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.CatalogUtils;

import java.io.File;
import java.util.logging.Logger;


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

public class RepositoryManager
{
  private static final Logger logger = Logger.getAnonymousLogger();

  public static final String METAENTRY = "metaRulesEntry";
  public static final String MANIFEST = "starManifest";
  public static final String COMPILED = "compiledEntry";

  public static CodeCatalog locatePackage(CodeRepository repository, ResourceURI uri) throws RepositoryException
  {
    RepositoryNode repoNode = repository.findNode(uri);

    if (repoNode != null) {
      CodeTree code = repoNode.getCode();
      if (code instanceof CodeCatalog)
        return (CodeCatalog) code;
      else if (!(code instanceof CodeCatalog))
        throw new RepositoryException("repository contains invalid entry for " + uri);
      else
        return ((CodeCatalog) code);
    } else
      return null;
  }

  public static boolean isPreambleURI(ResourceURI uri)
  {
    return uri.getScheme().equals(Resources.STDSCHEME);
  }

  public static Catalog lookForCatalog(ResourceURI uri, Catalog fallback)
  {
    try {
      return CatalogUtils.parseCatalog(uri, fallback);
    } catch (Exception e) {
      return fallback;
    }
  }

  public static MetaRules locateMeta(CodeRepository repository, ResourceURI uri)
  {
    try {
      CodeCatalog imported = locatePackage(repository, uri);
      if (imported != null) {
        CodeTree importMeta = imported.resolve(METAENTRY, MetaRules.EXTENSION);
        if (importMeta instanceof MetaRules)
          return (MetaRules) importMeta;
      }
    } catch (RepositoryException e) {
      logger.info("Cannot locate " + uri + " in repository");
    }
    return null;
  }

  public static Manifest locateStarManifest(CodeRepository repository, ResourceURI uri) throws ResourceException,
      CatalogException, RepositoryException
  {
    CodeCatalog imported = locatePackage(repository, uri);
    if (imported != null) {
      CodeTree importMeta = imported.resolve(MANIFEST, Manifest.EXTENSION);
      if (importMeta instanceof Manifest)
        return (Manifest) importMeta;
    }
    return null;
  }

  public static CafeManifest locateCafeManifest(CodeRepository repository, ResourceURI uri)
  {
    CodeTree codeCatalog = repository.findCode(uri);

    if (codeCatalog instanceof CodeCatalog) {
      try {
        CodeTree manifestEntry = ((CodeCatalog) codeCatalog).resolve(Names.CAFE_MANIFEST, CafeManifest.EXTENSION);
        if (manifestEntry instanceof CafeManifest)
          return (CafeManifest) manifestEntry;
      } catch (RepositoryException e) {
        return null;
      }
    }

    return null;
  }

  public static ClassRoot locateClassRoot(CodeRepository repository, Location loc, ResourceURI uri, ErrorReport errors)
  {
    CodeTree codeCatalog = repository.findCode(uri);

    if (codeCatalog instanceof CodeCatalog) {
      try {
        CodeTree entry = ((CodeCatalog) codeCatalog).resolve(Names.CLASS_ROOT, ClassRoot.EXTENSION);
        if (entry instanceof ClassRoot)
          return (ClassRoot) entry;
      } catch (RepositoryException e) {
        errors.reportError("cannot access " + uri + "\nbecause " + e.getMessage(), loc);
      }
    } else
      errors.reportError("cannot access " + uri, loc);

    return null;
  }

  public static CodeRepository setupStandardRepository(ClassLoader defltLoader)
  {
    try {
      return setupRepository(StarCompiler.STAR_PATH, StarMain.TARGET_DIR, defltLoader);
    } catch (LanguageException e) {
      ErrorReport errors = e.getMessages();
      System.err.println(errors);
      return null;
    }
  }

  public static CodeRepository setupRepository(String starPath, String targetDir, ClassLoader defltLoader)
      throws LanguageException
  {
    ErrorReport errors = new ErrorReport();

    try {
      CodeRepository tgtRepository;
      if (!StringUtils.isTrivial(targetDir))
        tgtRepository = new DirectoryRepository(new File(targetDir), false, true, defltLoader, errors);
      else
        tgtRepository = new CodeRepositoryImpl(defltLoader, true, errors);

      if (!StringUtils.isTrivial(starPath))
        tgtRepository = new CompositeRepository(starPath, tgtRepository, false, defltLoader, errors);

      return tgtRepository;
    } catch (RepositoryException e) {
      errors.reportWarning(e.getMessage());
      return null;
    } finally {
      if (!errors.isErrorFree())
        throw new LanguageException(errors);
      if (StarCompiler.SHOW_PRELUDE && StarCompiler.SHOWTIMING)
        System.err.println(errors);
    }
  }
}
