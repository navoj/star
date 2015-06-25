package org.star_lang.star.array;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
