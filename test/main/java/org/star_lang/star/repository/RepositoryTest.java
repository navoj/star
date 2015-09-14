package org.star_lang.star.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.star_lang.star.LanguageException;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.CompositeRepository;
import org.star_lang.star.code.repository.DirectoryRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.TemplateString;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.CatalogTransducer;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.CatalogUtils;
import org.star_lang.star.resource.catalog.MemoryCatalog;

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

public class RepositoryTest extends SRTest
{
  public RepositoryTest() throws RepositoryException
  {
    super(RepositoryTest.class, new DirectoryRepository(createDir(), false, true, Thread.currentThread()
        .getContextClassLoader(), new ErrorReport()));
  }

  /*
   * Set up a test of file repositories.
   */
  @Test
  public void testRepository() throws ResourceException, IOException, CatalogException
  {
    Map<String, String> vars = new HashMap<>();

    File bDir = createDir();
    File bFile = copyFile(bDir, "B.star"); // We need its URI

    vars.put("B", URIUtils.createFileURI(bFile).toString());

    String aCat = TemplateString.stringTemplate(
        "catalog{\n  content is hash{\n    \"A\"->\"AR.star\";\n    \"B\"->\"$B\";\n  }\n}", vars);

    File aDir = createDir();
    File aFile = copyFile(aDir, "AR.star");
    File aCatFile = FileUtil.writeFile(new File(aDir, "catalog"), aCat);

    ApplicationProperties.setWd(URIUtils.createFileURI(new File(aDir, "catalog")));

    Catalog testCat = CatalogUtils.catalogInDirectory(URIUtils.createFileURI(aCatFile), aDir, StarRules.starCatalog());
    ResourceURI uri = URIUtils.createFileURI(aFile);

    runStar(uri, testCat);
    run(uri);
  }

  @Test
  public void testVersioning() throws CatalogException, EvaluationException, ResourceException, RepositoryException,
      LanguageException
  {
    String CatScheme = "cat";
    String v0 = "v is package{ def version is 0.0}";
    String v1 = "v is package{ def version is 1.0}";
    String v2 = "v is package{ def version is 1.1}";
    String q = "q is package{ import v; prc main() do { logMsg(info,\"version $version\"); assert version=1.1} }";

    ResourceURI u0 = URIUtils.createQuotedURI("v", v0, StandardNames.VERSION + "=0.0");
    ResourceURI u1 = URIUtils.createQuotedURI("v", v1, StandardNames.VERSION + "=1.0");
    ResourceURI u2 = URIUtils.createQuotedURI("v", v2, StandardNames.VERSION + "=1.1");
    ResourceURI qu = URIUtils.createQuotedURI("q", q);

    Map<String, ResourceURI> entries = new HashMap<>();
    entries.put("v0", u0);
    entries.put("v1", u1);
    entries.put("v2", u2);
    entries.put("v", URIUtils.create(CatScheme, "v2"));

    Catalog cat = new MemoryCatalog("test", null, u1, StarRules.starCatalog(), entries);

    Resources.recordTransducer(CatScheme, new CatalogTransducer(cat));

    CodeRepository repository = StarMain.standardRepository();
    ErrorReport errors = new ErrorReport();

    StarMake.compile(repository, u1, cat, errors);
    StarMake.compile(repository, u2, cat, errors);

    StarMake.compile(repository, qu, cat, errors);
    assert errors.isErrorFree();
    StarMain.run(repository, qu, new IValue[] {});
  }

  @Test
  public void refreshLoader() throws RepositoryException
  {
    CodeRepository standardRepository = StarMain.standardRepository();

    ErrorReport errors = new ErrorReport();
    ClassLoader defltLoader = Thread.currentThread().getContextClassLoader();

    CompositeRepository repository = new CompositeRepository(new ArrayList<CodeRepository>(), standardRepository,
        false, defltLoader, errors);
    Catalog catalog = StarRules.starCatalog();

    String src = "test is package { type test is test { s has type string; }  }";
    StarMake.compile(repository, URIUtils.createQuotedURI("test", src), catalog, errors);

    repository.refreshClassLoader();

    StarMake.compile(repository, URIUtils.createQuotedURI("test2", src), catalog, errors);
  }
}
