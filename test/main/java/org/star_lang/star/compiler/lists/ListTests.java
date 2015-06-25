package org.star_lang.star.compiler.lists;

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
public class ListTests extends SRTest
{
  public ListTests()
  {
    super(ListTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testPick()
  {
    runStar("pick.star");
  }

  @Test
  public void testLists()
  {
    runStar("listtests.star");
  }

  @Test
  public void testSelections()
  {
    runStar("selections.star");
  }

  @Test
  public void testMatchList()
  {
    runStar("matchlist.star");
  }

  @Test
  public void testMatchingLists()
  {
    runStar("matchComp.star");
  }

  @Test
  public void testIndexing()
  {
    runStar("indextest.star");
  }

  @Test
  public void testDiffContract()
  {
    runStar("diff.star");
  }

  @Test
  public void testPeopleSort()
  {
    runStar("peoplesort.star");
  }

  @Test
  public void testSortContract()
  {
    runStar("sorting.star");
  }

  @Test
  public void testImportContract()
  {
    runStar("testsortcontract.star");
  }

  @Test
  public void testMultiVars()
  {
    runStar("multivars.star");
  }

  @Test
  public void badListSyntax()
  {
    exception.expectMessage("unexpected ','");

    runStar("badList.star");
  }

  @Test
  public void testListEx()
  {
    runStar("listEx.star");
  }

  @Test
  public void fingerListsTest()
  {
    runStar("fingerTest.star");
  }

  @Test
  public void testLexDigits()
  {
    runStar("lexdigit.star");
  }

  @Test
  public void benchmark()
  {
    runStar("bench.star");
  }

  @Test
  public void testIndex()
  {
    runStar("listindex.star");
  }

  @Test
  public void testSequences()
  {
    runStar("sequencetests.star");
  }

  @Test
  public void testMember()
  {
    runStar("nonmember.star");
  }

  @Test
  public void testSplices()
  {
    if (!COMPILE_ONLY)
      exception.expectMessage("should be greater than or equal to index");

    runStar("splices.star");
  }

  @Test
  public void testArrayLiteral()
  {
    runStar("arrayliteral.star");
  }

  @Test
  public void testFolds()
  {
    runStar("foldingtest.star");
  }

  @Test
  public void testDependencies()
  {
    runStar("dependency.star");
  }

  @Test
  public void testDoubleConc()
  {
    runStar("doubleconc.star");
  }
}
