package org.star_lang.star.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CodeRepositoryImpl;
import org.star_lang.star.code.repository.CompositeRepository;
import org.star_lang.star.code.repository.DirectoryRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryNode;
import org.star_lang.star.code.repository.zip.ZipCodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;

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

public class CompositeRepositoryTest extends SRTest
{
  private static final File repoDir = createDir();
  private static final ErrorReport errors = new ErrorReport();

  public CompositeRepositoryTest() throws RepositoryException
  {
    super(CompositeRepositoryTest.class, new DirectoryRepository(repoDir, false, true, Thread.currentThread()
        .getContextClassLoader(), errors));
  }

  @Test
  public void testCompositeRepository() throws IOException, ResourceException, RepositoryException,
      EvaluationException, CatalogException, LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile.star");

    URIUtils.setupStarURI(uriTestDir);

    ResourceURI innerURI = ResourceURI.parseURI("star:starview.com/URITestFile/_");
    Catalog catalog = StarRules.starCatalog();

    // Make sure compiling doesn't fail.
    try {
      StarCompiler.compile(innerURI, catalog, repository);
      run(innerURI);
    } catch (CatalogException | RepositoryException e) {
      fail();
    }
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    CodeRepository newRepo = new CompositeRepository(repoDir.getCanonicalPath(), repository, false, loader, errors);
    RepositoryNode node = newRepo.findNode(innerURI);
    assertNotNull(node);
    StarMain.run(newRepo, innerURI, new IValue[] {});
  }

  @Test
  public void testCompositeRepositoryImports() throws IOException, ResourceException, LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");

    System.out.println("test dir" + uriTestDir);

    URIUtils.setupStarURI(uriTestDir);

    ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile3");
    ErrorReport errors = new ErrorReport();
    Catalog catalog = StarRules.starCatalog();
    try {
      StarCompiler.compile(srcURI, catalog, repository);
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      CodeRepository newRepository = new CompositeRepository(URIUtils.uriFilePath(repoDir), repository, false, loader,
          errors);
      File newDir = createDir();
      File newFile = copyFile(newDir, "URITestFile4.star");
      ResourceURI newUri = URIUtils.createFileURI(newFile);
      StarCompiler.compile(newUri, catalog, newRepository);
      assertTrue(errors.isWarningAndErrorFree());
      StarMain.run(newRepository, newUri, new IValue[] {});
    } catch (CatalogException | RepositoryException | EvaluationException e) {
      fail();
    }
  }

  @Test
  public void testZipRepository() throws IOException, ResourceException, LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");
    copyFile(uriTestDir, "URITestFile4.star");

    URIUtils.setupStarURI(uriTestDir);

    File zipTestDir = createDir();

    Catalog catalog = StarRules.starCatalog();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile4");
      StarMake.compile(new DirectoryRepository(zipTestDir, false, true, errors), srcURI, catalog, errors);
      File zipTestFile = ZipCodeRepository.createZarFromDir(zipTestDir);
      CodeRepository newRepository = new ZipCodeRepository(zipTestFile, loader, errors);
      StarMain.run(newRepository, srcURI, new IValue[] {});
      assertTrue(errors.isWarningAndErrorFree());
    } catch (CatalogException e) {
      fail();
    } catch (RepositoryException e) {
      fail();
    } catch (EvaluationException e) {
      fail();
    }
  }

  @Test
  public void testCompositeRepositoryWithZipRepository() throws IOException, ResourceException, LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");

    URIUtils.setupStarURI(uriTestDir);

    File zipTestDir = createDir();

    Catalog catalog = StarRules.starCatalog();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile3");
      DirectoryRepository dirRepo = new DirectoryRepository(zipTestDir, false, true, loader, errors);
      StarCompiler.compile(srcURI, catalog, dirRepo);
      File zipTestFile = ZipCodeRepository.createZarFromDir(zipTestDir);
      ArrayList<CodeRepository> repoList = new ArrayList<CodeRepository>();
      repoList.add(new ZipCodeRepository(zipTestFile, loader, errors));
      CodeRepository newRepository = new CompositeRepository(repoList, new CodeRepositoryImpl(loader, false, errors),
          false, loader, errors);
      File newDir = createDir();
      File newFile = copyFile(newDir, "URITestFile4.star");
      ResourceURI newUri = URIUtils.createFileURI(newFile);
      StarCompiler.compile(newUri, catalog, newRepository);
      StarMain.run(newRepository, newUri, new IValue[] {});
      assertTrue(errors.isWarningAndErrorFree());
    } catch (CatalogException e) {
      fail();
    } catch (RepositoryException e) {
      fail();
    } catch (EvaluationException e) {
      fail();
    }
  }

  @Test
  public void testCompositeRepositoryWithZipInputStreamRepository() throws IOException, ResourceException,
      LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");
    URIUtils.setupStarURI(uriTestDir);

    File zipTestDir = createDir();

    Catalog catalog = StarRules.starCatalog();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile3");
      StarCompiler.compile(srcURI, catalog, new DirectoryRepository(zipTestDir, false, true, loader, errors));

      File zipTestFile = ZipCodeRepository.createZarFromDir(zipTestDir);
      ArrayList<CodeRepository> repoList = new ArrayList<CodeRepository>();
      repoList.add(new ZipCodeRepository(new FileInputStream(zipTestFile), loader, zipTestFile.getAbsolutePath(),
          errors));
      CodeRepository newRepository = new CompositeRepository(repoList, new CodeRepositoryImpl(loader, false, errors),
          false, loader, errors);
      File newDir = createDir();
      File newFile = copyFile(newDir, "URITestFile4.star");
      ResourceURI newUri = URIUtils.createFileURI(newFile);
      StarCompiler.compile(newUri, catalog, newRepository);
      StarMain.run(newRepository, newUri, new IValue[] {});
      assertTrue(errors.isWarningAndErrorFree());
    } catch (CatalogException e) {
      fail();
    } catch (RepositoryException e) {
      fail();
    } catch (EvaluationException e) {
      fail();
    }
  }

  @Test
  public void testCompositeRepositoryWithZipRepositoryWithMetaInf() throws IOException, ResourceException,
      LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");
    URIUtils.setupStarURI(uriTestDir);

    File zipTestDir = createDir();

    ErrorReport errors = new ErrorReport();
    Catalog catalog = StarRules.starCatalog();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile3");
      StarCompiler.compile(srcURI, catalog, new DirectoryRepository(zipTestDir, false, true, loader, errors));
      File metaInf = new File(zipTestDir, "META-INF");
      metaInf.mkdir();
      File metaInfSub = new File(metaInf, "sub");
      FileOutputStream str = new FileOutputStream(metaInfSub);
      str.write(new byte[] { 1, 2, 3 });
      str.close();
      File zipTestFile = ZipCodeRepository.createZarFromDir(zipTestDir);
      ArrayList<CodeRepository> repoList = new ArrayList<CodeRepository>();
      repoList.add(new ZipCodeRepository(zipTestFile, loader, errors));
      CodeRepository newRepository = new CompositeRepository(repoList, new CodeRepositoryImpl(loader, false, errors),
          false, loader, errors);
      File newDir = createDir();
      File newFile = copyFile(newDir, "URITestFile4.star");
      ResourceURI newUri = URIUtils.createFileURI(newFile);
      StarCompiler.compile(newUri, catalog, newRepository);
      StarMain.run(newRepository, newUri, new IValue[] {});
      assertTrue(errors.isWarningAndErrorFree());
    } catch (CatalogException e) {
      fail();
    } catch (RepositoryException e) {
      fail();
    } catch (EvaluationException e) {
      fail();
    }
  }

  @Test
  public void testCompositeRepositoryIterator() throws IOException, ResourceException, LanguageException
  {
    File uriTestDir = createDir();
    copyFile(uriTestDir, "URITestFile3.star");
    URIUtils.setupStarURI(uriTestDir);

    File zipTestDir = createDir();

    ErrorReport errors = new ErrorReport();
    Catalog catalog = StarRules.starCatalog();
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ResourceURI srcURI = ResourceURI.parseURI("star:starviewinc.com/URITestFile3");
      StarCompiler.compile(srcURI, catalog, new DirectoryRepository(zipTestDir, false, true, loader, errors));
      File zipTestFile = ZipCodeRepository.createZarFromDir(zipTestDir);
      ArrayList<CodeRepository> repoList = new ArrayList<CodeRepository>();
      repoList.add(new ZipCodeRepository(zipTestFile, loader, errors));
      CodeRepository newRepository = new CompositeRepository(repoList, new CodeRepositoryImpl(loader, false, errors),
          false, loader, errors);
      File newDir = createDir();
      File newFile = copyFile(newDir, "URITestFile4.star");
      ResourceURI newUri = URIUtils.createFileURI(newFile);
      StarCompiler.compile(newUri, catalog, newRepository);
      StarMain.run(newRepository, newUri, new IValue[] {});
      assertTrue(errors.isWarningAndErrorFree());
      boolean seenTransformer = false;
      for (RepositoryNode node : newRepository) {
        if (node.getUri().equals(srcURI))
          seenTransformer = true;
      }
      assertTrue(seenTransformer);
    } catch (CatalogException e) {
      fail();
    } catch (RepositoryException e) {
      fail();
    } catch (EvaluationException e) {
      fail();
    }
  }
}
