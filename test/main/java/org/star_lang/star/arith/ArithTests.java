package org.star_lang.star.arith;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class ArithTests extends SRTest
{
  public ArithTests()
  {
    super(ArithTests.class);
  }

  @Test
  public void testFactorial()
  {
    runStar("factorial.star");
  }

  @Test
  public void testTak()
  {
    runStar("tak.star");
  }

  @Test
  public void testArithImport()
  {
    runStar("arithtest.star");
  }

  @Test
  public void testArithMutual()
  {
    runStar("arith1.star");
  }

  @Test
  public void testLargeNumbers()
  {
    runStar("largenumbers.star");
  }

  @Test
  public void testMatchMinus()
  {
    runStar("matchMinus.star");
  }

  @Test
  public void testFibonacci()
  {
    runStar("fibonacci.star");
  }

  @Test
  public void testMathLib()
  {
    runStar("mathLibTest.star");
  }

  @Test
  public void testNaive()
  {
    runStar("naive.star");
  }

  @Test
  public void testLetCast()
  {
    runStar("letCast.star");
  }

  @Test
  public void testFact()
  {
    runStar("factCurry.star");
  }

  @Test
  public void testFixed()
  {
    runStar("fixedSqrt.star");
  }

  @Test
  public void loopTest()
  {
    runStar("loopTest.star");
  }
}
