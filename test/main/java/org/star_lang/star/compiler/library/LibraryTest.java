package org.star_lang.star.compiler.library;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.star_lang.star.StarRules;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.compiler.util.ApplicationProperties;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.TemplateString;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.Catalog;
import org.star_lang.star.resource.catalog.CatalogException;
import org.star_lang.star.resource.catalog.CatalogUtils;

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
public class LibraryTest extends SRTest
{
  public LibraryTest()
  {
    super(LibraryTest.class);
  }

  /*
   * Set up a test of library importing. We create two directories, one with the library in it, and
   * another with the test in it. We have to do it this way to avoid getting everything messed up.
   */
  @Test
  public void testLibrary() throws ResourceException, IOException, CatalogException
  {
    File libDir = createDir("catalog", "personLib.star", "sorting.star");
    File manifest = copyFile(libDir, "libmanifest.star");

    File tstDir = createDir();
    File libTest = copyFile(tstDir, "libtest.star");
    File persons = copyFile(tstDir, "persons.star");
    Map<String, String> vars = new HashMap<String, String>();
    vars.put("libtest", URIUtils.createFileURI(libTest).toString());
    vars.put("persons", URIUtils.createFileURI(persons).toString());
    vars.put("libmanifest", URIUtils.createFileURI(manifest).toString());

    String tstCat = TemplateString
        .stringTemplate(
            "test is catalog{\n  content is hash{\n    \"libtest\"->\"$libtest\";\n      \"persons\"->\"$persons\";\n    \"libmanifest\"->\"$libmanifest\";\n  }\n}",
            vars);
    File tstCatFile = FileUtil.writeFile(new File(tstDir, "catalog"), tstCat);

    ApplicationProperties.setWd(URIUtils.createFileURI(new File(libDir, "catalog")));

    Catalog testCat = CatalogUtils.catalogInDirectory(URIUtils.createFileURI(tstCatFile), tstDir, StarRules
        .starCatalog());

    runStar(URIUtils.createFileURI(libTest), testCat);
  }

  /*
   * A Variation where we compile the library. Then destroy the source to see if the import still
   * works.
   */
  @Test
  public void testCodeImport() throws ResourceException, IOException, CatalogException
  {
    File libDir = createDir("catalog", "personLib.star", "sorting.star");
    File manifest = copyFile(libDir, "libmanifest.star");

    File tstDir = createDir();
    Map<String, String> vars = new HashMap<String, String>();
    ResourceURI libTestURI = URIUtils.createFileURI(copyFile(tstDir, "libtest.star"));
    ResourceURI personUri = URIUtils.createFileURI(copyFile(tstDir, "persons.star"));
    vars.put("libtest", libTestURI.toString());
    vars.put("persons", personUri.toString());
    vars.put("libmanifest", URIUtils.createFileURI(manifest).toString());

    String tstCat = TemplateString
        .stringTemplate(
            "test is catalog{\n  content is hash{\n    \"libtest\"->\"$libtest\";\n    \"persons\"->\"$persons\";\n    \"libmanifest\"->\"$libmanifest\";\n  }\n}",
            vars);
    File tstCatFile = FileUtil.writeFile(new File(tstDir, "catalog"), tstCat);

    ApplicationProperties.setWd(URIUtils.createFileURI(new File(libDir, "catalog")));

    Catalog testCat = CatalogUtils.catalogInDirectory(URIUtils.createFileURI(tstCatFile), tstDir, StarRules
        .starCatalog());

    compile(personUri, testCat);

    removeFile(tstDir, "persons.star");

    runStar(libTestURI, testCat);
  }
}
