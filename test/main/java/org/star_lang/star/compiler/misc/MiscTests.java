package org.star_lang.star.compiler.misc;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

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
public class MiscTests extends SRTest
{
  public MiscTests()
  {
    super(MiscTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none().handleAssertionErrors();

  @Test
  public void testAssertions()
  {
    runStar("assertions.star");
  }

  @Test
  public void testIterators()
  {
    runStar("iterations.star");
  }

  @Test
  public void testTrees()
  {
    runStar("trees.star");
  }

  @Test
  public void testBinaryTrees()
  {
    runStar("binaryTrees.star");
  }

  @Test
  public void testLargeLists()
  {
    runStar("largeList.star");
  }

  @Test
  public void testExcept()
  {
    runStar("except.star");
  }

  @Test
  public void testSequencePtns()
  {
    runStar("sequencePtns.star");
  }

  @Test
  public void testSequenceElim()
  {
    runStar("elim.star");
  }

  @Test
  public void testFreePtn()
  {
    runStar("freePtn.star");
  }

  @Test
  public void testSubstitute()
  {
    runStar("substTest.star");
  }

  @Test
  public void testEqualGen()
  {
    runStar("eqgenerate.star");
  }

  @Test
  public void testPP()
  {
    runStar("testPP.star");
  }

  @Test
  public void testKK()
  {
    runStar("konspp.star");
  }

  @Test
  public void testBadField()
  {
    exception.expectMessage("not known to have field 'foo'");

    runStar("badfield.star");
  }

  @Test
  public void testTypeInLet()
  {
    runStar("lettype.star");
  }

  @Test
  public void testWhileCons()
  {
    runStar("whilerev.star");
  }

  @Test
  public void funnyTestable()
  {
    exception.expectMessage("unresolved method: (size from sizeable");
    runStar("funnyTestable.star");
  }

  @Test
  public void testComparables()
  {
    runStar("comparables.star");
  }

  @Test
  public void testDeepRecord()
  {
    runStar("deepRecord.star");
  }

  @Test
  public void testRecordThis()
  {
    runStar("recordThis.star");
  }

  @Test
  public void testAttrsRecord()
  {
    runStar("valofsrch.star");
  }

  @Test
  public void testDisplayAnon()
  {
    runStar("displayAnon.star");
  }

  @Test
  public void testRegressions()
  {
    runStar("regressions.star");
  }

  @Test
  public void testFunProc()
  {
    runStar("funproc.star");
  }

  @Test
  public void testArgFree()
  {
    runStar("freeInArg.star");
  }

  @Test
  public void testTcBug()
  {
    exception.expectMessage("is read only");
    runStar("tcbug.star");
  }

  @Test
  public void defltFloatBug()
  {
    runStar("floatDefltBug.star");
  }

  @Test
  public void testTplPtns()
  {
    runStar("tplPtns.star");
  }

  @Test
  public void otherTest()
  {
    runStar("othertest.star");
  }

  @Test
  public void disjuncTest()
  {
    runStar("disjunc.star");
  }

  @Test
  public void expectMatchTest()
  {
    exception.expectMessage("because a is read only");
    runStar("expect.star");
  }

  @Test
  public void testBadTplMatch()
  {
    exception.expectMessage("not consistent with ");

    runStar("tplbadmatch.star");
  }

  @Test
  public void testDisplay() throws EvaluationException
  {
    IValue list = Factory.newArray(StandardTypes.stringType, Factory.newString("one"), Factory.newString("two"),
        Factory.newString("three"));
    Assert.assertEquals("list of [\"one\", \"two\", \"three\"]", ValueDisplay.display(list));
  }

  @Test
  public void testMatching()
  {
    runStar("matchingtest.star");
  }

  @Test
  public void testMatchRecord()
  {
    runStar("matchingRecord.star");
  }

  @Test
  public void testMatchQuote()
  {
    runStar("matchquote.star");
  }

  @Test
  public void testNestedTupleVars()
  {
    runStar("nestedTupleVars.star");
  }

  @Test
  public void testLetTuple()
  {
    runStar("nestedLetTuple.star");
  }

  @Test
  public void testWhileLoopTerm()
  {
    runStar("earlyWhile.star");
  }

  @Test
  public void testOrWhileLoopTerm()
  {
    runStar("orWhile.star");
  }

  @Test
  public void testBadWhileLoop()
  {
    exception.expectMessage("'tail' does not seem to be declared");
    runStar("badWhile.star");
  }

  @Test
  public void testbinaryCoerce()
  {
    runStar("binarycoerce.star");
  }

  @Test
  public void testHashCollision()
  {
    runStar("unsorted.star");
  }

  @Test
  public void testCaseTgt()
  {
    exception.expectMessage("not guaranteed to return a value");
    runStar("caseTgt.star");
  }

  @Test
  public void testComplexConditional()
  {
    runStar("complexconditional.star");
  }

  @Test
  public void testRecordPtn()
  {
    runStar("recordVarsPtn.star");
  }

  @Test
  public void testDoubleSel()
  {
    runStar("doubleSel.star");
  }

  @Test
  public void runCallCall()
  {
    runStar("callCall.star");
  }

  @Test
  public void locationBug()
  {
    exception.expectMessage("already defined");
    runStar("locationbug.star");
  }

  @Test
  public void testApply()
  {
    runStar("applicTest.star");
  }

  @Test
  public void testAnonFun()
  {
    runStar("anonFun.star");
  }

  @Test
  public void testFunLiteral()
  {
    runStar("pr2.star");
  }

  @Test
  public void testForLoop()
  {
    runStar("forLoopTest.star");
  }

  @Test
  public void testDeflt()
  {
    runStar("defltTest.star");
  }

  @Test
  public void testHashCodeVerify()
  {
    exception.expectMessage("cannot use raw type integer_");
    runStar("hashCodeVerify.star");
  }

  @Test
  public void insufficientValof()
  {
    exception.expectMessage("not guaranteed to return a value");
    runStar("funnyCase.star");
  }

  @Test
  public void testCasebug()
  {
    runStar("casebug.star");
  }

  @Test
  public void testNoTerminate()
  {
    exception.expectMessage("'maybeBind' does not seem to be declared");
    runStar("noTerminate.star");
  }

  @Test
  public void testCaseScopes()
  {
    exception.expectMessage("variable a is not permitted in this pattern because it is declared at");
    runStar("casescopes.star");
  }

  @Test
  public void testSmallPkg()
  {
    runStar("smallPkg.star");
  }

  @Test
  public void classCastTest()
  {
    runStar("fingerClassCast.star");
  }

  @Test
  public void redblackTest()
  {
    runStar("redblack.star");
  }

  @Test
  public void infosetTest()
  {
    runStar("infosetTest.star");
  }

  @Test
  public void jsonMacroTest()
  {
    runStar("jsonMacro.star");
  }

  @Test
  public void anotherJsonTest()
  {
    runStar("anotherJson.star");
  }

  @Test
  public void testApplyExp()
  {
    runStar("applyExpTest.star");
  }

  @Test
  public void extractFooTest()
  {
    runStar("extractFoo.star");
  }

  @Test
  public void testFunnyDefault()
  {
    runStar("defaultAssign.star");
  }

  @Test
  public void testWhilePerf()
  {
    runStar("whilePerf.star");
  }

  @Test
  public void testShortest()
  {
    runStar("shortestPath.star");
  }

  @Test
  public void testTupleAssign()
  {
    runStar("tupleAssign.star");
  }
}
