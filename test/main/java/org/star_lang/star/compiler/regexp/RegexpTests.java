package org.star_lang.star.compiler.regexp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;

public class RegexpTests extends SRTest
{
  public RegexpTests()
  {
    super(RegexpTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testDollar()
  {
    runStar("dollarRegexp.star");
  }

  @Test
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
