package org.star_lang.star.patterns;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

public class PtnTest extends SRTest
{
  public PtnTest()
  {
    super(PtnTest.class);
  }

  @Test
  public void testPtns()
  {
    runStar("ptest1.star");
  }

  @Test
  public void testRegExpPtn()
  {
    runStar("regpttrn.star");
  }

  @Test
  public void testEvenPrime()
  {
    runStar("evenPrimes.star");
  }
}
