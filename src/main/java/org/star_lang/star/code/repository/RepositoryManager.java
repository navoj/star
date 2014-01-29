package org.star_lang.star.code.repository;

import java.io.File;

import org.star_lang.star.CompileDriver;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarMain;
import org.star_lang.star.code.Manifest;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CafeManifest;
import org.star_lang.star.compiler.cafe.compile.ClassRoot;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.type.Location;
import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.Resources;
import com.starview.platform.resource.catalog.Catalog;
import com.starview.platform.resource.catalog.CatalogException;
import com.starview.platform.resource.catalog.CatalogUtils;

/**
 * The RepositoryManager helps the Star compiler to figure out the details of compiling and
 * importing code
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

public class RepositoryManager
{
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

  public static CodeCatalog locatePackage(CodeRepository repository, String name, Catalog catalog)
      throws RepositoryException
  {
    try {
      ResourceURI uri = catalog.resolve(name);
      if (uri != null)
        return locatePackage(repository, uri);
    } catch (CatalogException e) {
    }

    return null;
  }

  public static Catalog lookForCatalog(ResourceURI uri, Catalog fallback, CodeRepository repository)
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
    }
    return null;
  }

  public static Manifest locateStarManifest(CodeRepository repository, IAbstract pkgName, Catalog catalog,
      ErrorReport errors) throws ResourceException, CatalogException, RepositoryException
  {
    CodeCatalog imported = locatePackage(repository, CompileDriver.uriOfPkgRef(pkgName, catalog));
    if (imported != null) {
      CodeTree importMeta = imported.resolve(MANIFEST, Manifest.EXTENSION);
      if (importMeta instanceof Manifest)
        return (Manifest) importMeta;
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
