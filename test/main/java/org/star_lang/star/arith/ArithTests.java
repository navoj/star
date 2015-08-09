package org.star_lang.star.arith;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
