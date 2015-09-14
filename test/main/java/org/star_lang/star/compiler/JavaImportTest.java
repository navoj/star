package org.star_lang.star.compiler;

import org.junit.Test;

public class JavaImportTest extends SRTest
{
  public JavaImportTest()
  {
    super(JavaImportTest.class);
  }

  @Test
  public void testSimpleJavaImport()
  {
    runStar("testJava.star");
  }
}
