package org.star_lang.star.asynchio;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.data.value.ResourceURI.URI;
import com.starview.platform.resource.URIUtils;

public class TestIO extends SRTest
{
  public TestIO()
  {
    super(TestIO.class);
  }

  protected void fileTest(String star, String file)
  {
    try {
      File libDir = createDir();
      ResourceURI localURI = URIUtils.createFileURI(copyFile(libDir, file));

      runStar(star, ((URI) localURI).getPath());
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testFileInput()
  {
    fileTest("basicFileInput.star", "sample.txt");
  }
}
