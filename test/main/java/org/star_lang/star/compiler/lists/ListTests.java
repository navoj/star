package org.star_lang.star.compiler.lists;

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
