package org.star_lang.star.compiler.regexp;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class RegexpTests extends SRTest
{
  public RegexpTests()
  {
    super(RegexpTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none().handleAssertionErrors();

  @Test
  public void testDollar()
  {
    runStar("dollarRegexp.star");
  }

  @Test
  @Ignore
  public void testTokens()
  {
    runStar("tokens.star");
  }

  @Test
  public void regexpTest()
  {
    runStar("regexp.star");
  }

  @Test
  public void testCondRegexp()
  {
    runStar("condRegexp.star");
  }

  @Test
  public void testDisjRegexp()
  {
    runStar("disjRegexp.star");
  }

  @Test
  public void testRegexpSearch()
  {
    runStar("regexpSearch.star");
  }

  @Test
  public void testRegCases()
  {
    runStar("regcases.star");
  }

  @Test
  public void testRegVar()
  {
    exception.expectMessage("'symbol' does not seem to be declared");
    runStar("regnonvar.star");
  }

  @Test
  public void testRegexpBug()
  {
    runStar("regbug.star");
  }

  @Test
  public void testNonstring()
  {
    runStar("nonStringRegexp.star");
  }
}
