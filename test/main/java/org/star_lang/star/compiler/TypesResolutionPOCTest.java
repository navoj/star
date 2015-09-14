package org.star_lang.star.compiler;

import org.junit.Test;

public class TypesResolutionPOCTest extends SRTest
{
  public TypesResolutionPOCTest()
  {
    super(TypesResolutionPOCTest.class);
  }

  @Test
  public void testQueryBug()
  {
    runStar("typesResolutionPOC.star");
  }
}
