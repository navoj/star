package org.star_lang.star.compiler.overload;

import org.junit.Ignore;
import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class OverloadTests extends SRTest
{
  public OverloadTests()
  {
    super(OverloadTests.class);
  }

  @Test
  public void testBasicOver()
  {
    runStar("fanpl.star");
  }

  @Test
  public void testActorOver()
  {
    runStar("actorOverload.star");
  }

  @Test
  @Ignore
  public void testDoubleOver()
  { // ignored for now.
    runStar("reducible.star");
  }

  @Test
  public void testContractInActor()
  {
    runStar("contractInActor.star");
  }

  @Test
  public void testOverloadedDefn()
  {
    runStar("overDefActor.star");
  }

  @Test
  public void testForEach()
  {
    runStar("forEach.star");
  }
}
