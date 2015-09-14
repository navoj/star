package org.star_lang.star.compiler;

import org.junit.Test;

public class ForLoopTests extends SRTest
{
  public ForLoopTests()
  {
    super(ForLoopTests.class);
  }

  @Test
  public void testForLoops()
  {
    runStar("forLoops.star");
  }

  @Test
  public void testRelForLoops()
  {
    runStar("relForLoops.star");
  }
}
