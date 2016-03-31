package org.star_lang.star.compiler.map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

  @Test
  public void mapMatching()
  {
    runStar("mapMatch.star");
  }
}
