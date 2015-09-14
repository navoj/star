package org.star_lang.star.custodians;

import org.junit.Ignore;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class FiberControlTest extends SRTest
{
  public FiberControlTest()
  {
    super(FiberControlTest.class);
  }

  @Test
  @Ignore("not really working yet")
  public void testFiberControl()
  {
    runStar("fiberControlTest.star");
  }

}
