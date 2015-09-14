package org.star_lang.star.queries;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class TestUpdate extends SRTest
{
  public TestUpdate()
  {
    super(TestUpdate.class);
  }

  @Test
  public void testUpdateRels()
  {
    runStar("updateRels.star");
  }

  @Test
  public void testUpdateCons()
  {
    runStar("updateCons.star");
  }

  @Test
  public void testActorCrud()
  {
    runStar("actorCrud.star");
  }
}
