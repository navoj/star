package org.star_lang.star.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.StarMain;
import org.star_lang.star.StarMake;
import org.star_lang.star.StarRules;
import org.star_lang.star.code.repository.DirectoryRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;
import org.star_lang.star.resource.catalog.CatalogException;

public class DirectoryRepoTest extends SRTest
{
  public DirectoryRepoTest()
  {
    super(DirectoryRepoTest.class);
  }

  @Test
  public void testDirectoryRepo() throws RepositoryException, EvaluationException, ResourceException, CatalogException,
      IOException
  {
    ErrorReport errors = new ErrorReport();
    File bDir = createDir();
    ClassLoader defltLoader = Thread.currentThread().getContextClassLoader();
    Map<String, String> vars = new HashMap<>();

    File testFile = copyFile(bDir, "test.star"); // We need its URI

    vars.put("B", URIUtils.createFileURI(testFile).toString());

    ResourceURI file = URIUtils.createFileURI(testFile);
    File output = createDir();

    DirectoryRepository repository = new DirectoryRepository(output, true, true, defltLoader, errors);
    StarMake.compile(repository, file, StarRules.starCatalog(), errors);

    IValue mainPackagePair = StarMain.ldPackage(repository, file, errors);
    Assert.assertNotNull(mainPackagePair); // NULL!!!
  }
}
