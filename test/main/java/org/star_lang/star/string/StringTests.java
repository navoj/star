package org.star_lang.star.string;

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
public class StringTests extends SRTest {
  public StringTests() {
    super(StringTests.class);
  }

  @Test
  public void stringConcatTest() {
    runStar("strings.star");
  }

  @Test
  public void testEmpty() {
    runStar("emptyInterpolate.star");
  }

  @Test
  public void indexTest() {
    runStar("strindex.star");
  }

  @Test
  public void quotedStringTest() {
    runStar("stringParse.star");
  }

  @Test
  public void stringCaseTest() {
    runStar("stringcases.star");
  }

  @Test
  public void splitStringTest() {
    runStar("stringsplit.star");
  }

  @Test
  public void stringFilter() {
    runStar("stringFilter.star");
  }

  @Test
  public void coerceStringTest() {
    runStar("coerce.star");
  }

  @Test
  public void stringSeqTest() {
    runStar("strsequence.star");
  }

  @Test
  public void charSeqTest() {
    runStar("charsequence.star");
  }

  @Test
  public void deserialTest() {
    runStar("deserialmap.star");
  }

  @Test
  public void testStringFind() {
    runStar("stringfind.star");
  }

  @Test
  public void testStringReverse() {
    runStar("stringReverse.star");
  }

  @Test
  public void testStringFormatting() {
    runStar("formats.star");
  }

  @Test
  public void testStringFloatFormatting() {
    runStar("floatformats.star");
  }

  @Test
  public void testBadStrings() {
    runStar("badStrings.star");
  }
}
