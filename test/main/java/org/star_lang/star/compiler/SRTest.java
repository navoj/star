package org.star_lang.star.compiler;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryManager;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.Resources.JarTransducer;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.URIBasedCatalog;

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

public abstract class SRTest
{
  public static final String TEST = "test";
  public static final boolean COMPILE_ONLY = ApplicationProperties.getProperty("COMPILE_ONLY", false);

  protected final Class<?> rootClass;
  protected final CodeRepository repository;

  public SRTest()
  {
    this(StarRules.class);
  }

  public SRTest(Class<?> rootClass)
  {
    this(rootClass, StarMain.standardRepository());
  }

  public SRTest(Class<?> rootClass, CodeRepository repository)
  {
    Resources.recordTransducer(TEST, new JarTransducer(rootClass));
    this.rootClass = rootClass;
    this.repository = repository;
  }

  protected void runStar(String testResource, String... args)
  {
    runStar(URIUtils.create(TEST, testResource), args);
  }

  protected void runStar(ResourceURI uri, String... args)
  {
    try {
      ResourceURI catalogURI = uri.resolve(Catalog.CATALOG);
      Catalog catalog = RepositoryManager.lookForCatalog(catalogURI, new URIBasedCatalog(uri.resolve("."), "", ""),
          repository);

      if (COMPILE_ONLY) {
        ErrorReport errors = new ErrorReport();
        StarMake.compile(repository, uri, catalog, errors);
        System.err.println(errors);
        if (!errors.isErrorFree())
          fail(errors.toString());
      } else
        StarMain.compileAndGo(repository, uri, catalog, strings(args));
    } catch (EvaluationException e) {
      fail("evaluation exception " + e.getMessage() + " at " + e.getLoc());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  protected void runStar(ResourceURI uri, Catalog catalog, String... args)
  {
    try {
      StarMain.compileAndGo(repository, uri, catalog, strings(args));
    } catch (EvaluationException e) {
      fail("eval violation " + e.getMessage() + " at " + e.getLoc());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  protected IValue[] strings(String[] strs)
  {
    IValue[] strings = new IValue[strs.length];
    for (int ix = 0; ix < strs.length; ix++)
      strings[ix] = Factory.newString(strs[ix]);
    return strings;
  }

  protected void compile(ResourceURI uri, Catalog catalog)
  {
    try {
      StarMain.compile(repository, uri, catalog);
    } catch (LanguageException e) {
      fail(e.getMessage());
    }
  }

  protected void run(ResourceURI uri)
  {
    try {
      ErrorReport report = StarMain.run(repository, uri, new IValue[] {});
      System.err.println(report);
    } catch (EvaluationException e) {
      fail("eval violation " + e.getMessage() + " at " + e.getLoc());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  protected static File createDir()
  {
    File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));

    if (!systemTmpDir.isDirectory())
      Assert.fail("could not access system temp directory");

    File tmpDir = new File(systemTmpDir, GenSym.genSym("star"));

    // sometimes the temporary directory is not released. Make sure it is empty.
    if (tmpDir.isDirectory())
      FileUtil.rmRf(tmpDir);
    if (!tmpDir.isDirectory() && !tmpDir.mkdir())
      Assert.fail("could not create temp directory");
    tmpDir.deleteOnExit();

    return tmpDir;
  }

  protected File createDir(String... files)
  {
    try {
      File tmpDir = createDir();

      for (String file : files)
        copyFile(tmpDir, file);

      return tmpDir;
    } catch (IOException e) {
      Assert.fail(e.getMessage());
      return null;
    }
  }

  protected File copyFile(File tmpDir, String name) throws IOException
  {
    try (InputStream rdr = StringUtils.getResourceStream(name, rootClass)) {
      if (rdr != null) {
        File tgt = new File(tmpDir, name);
        tgt.deleteOnExit();
        try (FileOutputStream wtr = new FileOutputStream(tgt)) {
          byte[] chBuff = new byte[1024];
          for (int len = rdr.read(chBuff); len > 0; len = rdr.read(chBuff))
            wtr.write(chBuff, 0, len);
        }
        return tgt;
      } else
        throw new IOException("cannot find resource " + name + " at " + rootClass);
    }
  }

  protected void removeFile(File tmpDir, String name)
  {
    File tgt = new File(tmpDir, name);
    tgt.delete();
  }
}
