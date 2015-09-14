package org.star_lang.star.array;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;

/**
 *
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
public class ArrayTests extends SRTest
{
  public ArrayTests()
  {
    super(ArrayTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testSimpleArray()
  {
    runStar("simpleArrayTests.star");
  }

  @Test
  public void testArrayCons()
  {
    runStar("arrayCons.star");
  }

  @Test
  public void testArraySort()
  {
    runStar("arraySort.star");
  }

  @Test
  public void testFilter()
  {
    runStar("arrayFilter.star");
  }

  @Test
  public void testIterate()
  {
    runStar("arrayIterate.star");
  }

  @Test
  public void testUpdates()
  {
    runStar("arrayUpdates.star");
  }

  @Test
  public void testConsVsArray()
  {
    runStar("consVsArray.star");
  }

  @Test
  public void testIndex()
  {
    runStar("arrayIndex.star");
  }

  @Test
  public void testSlice()
  {
    runStar("arrayslice.star");
  }

  @Test
  public void testSets()
  {
    runStar("arraysets.star");
  }

  @Test
  public void testBounds()
  {
    runStar("arraybounds.star");
  }

  @Test
  public void testArrayIndexMatch()
  {
    runStar("arrayIndexMatch.star");
  }

  @Test
  public void testAltArray()
  {
    runStar("altArrayTest.star");
  }
}
