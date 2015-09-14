package org.star_lang.star.relations;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class TestRelations extends SRTest
{
  public TestRelations()
  {
    super(TestRelations.class);
  }

  @Test
  public void testRiskRels()
  {
    runStar("risk.star");
  }

  @Test
  public void testMaster()
  {
    runStar("mastermind.star");
  }
}
