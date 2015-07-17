package org.star_lang.star.compiler.sets;

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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;

/**
 * Created by fgm on 7/15/15.
 */
public class SetTests extends SRTest {
  public SetTests(){
    super(SetTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testBasic()
  {
    runStar("basicsets.star");
  }

  @Test
  public void testExtend()
  {
    runStar("complexkey.star");
  }

  @Test
  public void testFloatSet()
  {
    runStar("floatSet.star");
  }

  @Test
  public void testSetMap()
  {
    runStar("setmap.star");
  }

  @Test
  public void largeSetTest()
  {
    runStar("largeSetTest.star");
  }
}
