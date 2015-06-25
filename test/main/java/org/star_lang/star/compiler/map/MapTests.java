package org.star_lang.star.compiler.map;

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
public class MapTests extends SRTest
{
  public MapTests()
  {
    super(MapTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testHash()
  {
    runStar("maptest.star");
  }

  @Test
  public void testMapEq()
  {
    runStar("mapeq.star");
  }

  @Test
  public void testNestedMap()
  {
    runStar("nestedMap.star");
  }

  @Test
  public void testMapEq2()
  {
    runStar("mapeq2.star");
  }

  @Test
  public void testMapComp()
  {
    runStar("mapcomp.star");
  }

  @Test
  public void testMapsearch()
  {
    runStar("mapsearch.star");
  }

  @Test
  public void testMapUpdate()
  {
    runStar("mapupdate.star");
  }

  @Test
  public void testMask()
  {
    runStar("masktest.star");
  }

  @Test
  public void testMerge()
  {
    runStar("mapmerge.star");
  }

  @Test
  public void testMapActors()
  {
    runStar("mapActors.star");
  }

  @Test
  public void testBasicTree()
  {
    runStar("basictreetest.star");
  }

  @Test
  public void testHashSet()
  {
    runStar("hashSetTest.star");
  }

  @Test
  public void testMapLoop()
  {
    runStar("mapLoopTest.star");
  }

  @Test
  public void testTreemap()
  {
    runStar("hashes.star");
  }

  @Test
  public void testFloatEq()
  {
    runStar("floatHash.star");
  }

  @Test
  public void testComplex()
  {
    runStar("complexkey.star");
  }

  @Test
  public void testTreemapUpdate()
  {
    runStar("treemapUpdate.star");
  }

  @Test
  public void testMapOfMaps()
  {
    runStar("mapOfMaps.star");
  }

  @Test
  public void testBadKey()
  {
    runStar("badkey.star");
  }

  @Test
  public void testMapFold()
  {
    runStar("mapFoldTest.star");
  }

  @Test
  public void testLargeMap()
  {
    runStar("largeMapTest.star");
  }

  @Test
  public void testMapInRecord()
  {
    runStar("recordMap.star");
  }

}
