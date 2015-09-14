package org.star_lang.star.compiler;

import org.junit.Test;

public class PtnTests extends SRTest
{
  public PtnTests()
  {
    super(PtnTests.class);
  }

  @Test
  public void testComboPtns()
  {
    runStar("comboPtns.star");
  }

  @Test
  public void testActionPtns()
  {
    runStar("intcase.star");
  }

  @Test
  public void testAssignment()
  {
    runStar("assignments.star");
  }

  @Test
  public void testMultAssignment()
  {
    runStar("multiAssign.star");
  }
}
