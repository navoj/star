package org.star_lang.star.compiler.library;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class SRImportTest extends SRTest
{
  public SRImportTest()
  {
    super(SRImportTest.class);
  }

  @Test
  public void testTypeImport()
  {
    runStar("importTest.star");
  }

  @Test
  public void tesConImport()
  {
    runStar("singlebar.star");
  }

  @Test
  public void testMainOverride()
  {
    runStar("mainOverride.star");
  }
}
