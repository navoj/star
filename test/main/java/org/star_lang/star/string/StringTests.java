package org.star_lang.star.string;

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
public class StringTests extends SRTest
{
  public StringTests()
  {
    super(StringTests.class);
  }

  @Test
  public void stringConcatTest()
  {
    runStar("strings.star");
  }

  @Test
  public void testEmpty()
  {
    runStar("emptyInterpolate.star");
  }

  @Test
  public void indexTest()
  {
    runStar("strindex.star");
  }

  @Test
  public void quotedStringTest()
  {
    runStar("stringParse.star");
  }

  @Test
  public void stringCaseTest()
  {
    runStar("stringcases.star");
  }

  @Test
  public void splitStringTest()
  {
    runStar("stringsplit.star");
  }

  @Test
  public void stringFilter()
  {
    runStar("stringFilter.star");
  }

  @Test
  public void coerceStringTest()
  {
    runStar("coerce.star");
  }

  @Test
  public void stringSeqTest()
  {
    runStar("strsequence.star");
  }

  @Test
  public void charSeqTest()
  {
    runStar("charsequence.star");
  }

  @Test
  public void deserialTest()
  {
    runStar("deserialmap.star");
  }

  @Test
  public void testStringFind()
  {
    runStar("stringfind.star");
  }

  @Test
  public void testStringReverse()
  {
    runStar("stringReverse.star");
  }

  @Test
  public void testStringFormatting()
  {
    runStar("formats.star");
  }

  @Test
  public void testStringFloatFormatting()
  {
    runStar("floatformats.star");
  }
}
