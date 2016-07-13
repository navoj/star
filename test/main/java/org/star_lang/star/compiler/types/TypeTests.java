package org.star_lang.star.compiler.types;

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
public class TypeTests extends SRTest
{
  public TypeTests()
  {
    super(TypeTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testSimpleExistenz()
  {
    runStar("existenz.star");
  }

  @Test
  public void testSafeCast()
  {
    runStar("safeCast.star");
  }

  @Test
  public void testExplicitAnnotations()
  {
    runStar("explicitAnnotation.star");
  }

  @Test
  public void testContractDefault()
  {
    runStar("defaultContracts.star");
  }

  @Test
  public void testInfer()
  {
    runStar("append.star");
  }

  @Test
  public void omegaTest()
  {
    runStar("omegatest.star");
  }

  @Test
  public void monadTest()
  {
    runStar("monadtest.star");
  }

  @Test
  public void reduceTest()
  {
    runStar("reduce.star");
  }

  @Test
  public void badRecord()
  {
    exception.expectMessage("p is a record specifier, not consistent with p(A, X)");
    runStar("badrecord.star");
  }

  @Test
  public void badField()
  {
    exception.expectMessage("not a type variable");
    runStar("badFieldConstraint.star");
  }

  @Test
  public void badCon1()
  {
    runStar("badCon1.star");
  }

  @Test
  public void badConstructor()
  {
    exception.expectMessage("already defined");
    runStar("badConTypes.star");
  }

  @Test
  public void groupTest()
  {
    runStar("group.star");
  }

  @Test
  public void testTypeConstr()
  {
    runStar("typeCon.star");
  }

  @Test
  public void testReftype()
  {
    runStar("reftype.star");
  }

  @Test
  public void testMapSeq()
  {
    runStar("mapseq.star");
  }

  @Test
  public void testNodeContract()
  {
    runStar("nodecontract.star");
  }

  @Test
  public void testOverEncap()
  {
    runStar("overloadencap.star");
  }

  @Test
  public void testEncapExample()
  {
    exception.expectMessage("c cannot be bound to integer");
    runStar("encapexample.star");
  }

  @Test
  public void testPrivate()
  {
    runStar("private.star");
  }

  @Test
  public void testVoid()
  {
    runStar("voidtests.star");
  }

  @Test
  public void testRecordAlias()
  {
    runStar("recordalias.star");
  }

  @Test
  public void testAnonRefRecord()
  {
    runStar("anonRefRecord.star");
  }

  @Test
  public void badFunType()
  {
    exception.expectMessage("invalid argument type");
    runStar("badfuntype.star");
  }

  @Test
  public void testEmptyRecord()
  {
    runStar("emptyRecord.star");
  }

  @Test
  public void testMutualAlias()
  {
    exception.expectMessage("are mutually recursive type aliases, which is not permitted");
    runStar("mutualAlias.star");
  }

  @Test
  public void badMapTest()
  {
    exception.expectMessage("dictionary expects 2 type arguments");
    runStar("badmapTest.star");
  }

  @Test
  public void voidFunTest()
  {
    runStar("voidfun.star");
  }

  @Test
  public void testSplayHeap()
  {
    runStar("splayTest.star");
  }

  @Test
  public void testAliasTypeValue()
  {
    runStar("weights.star");
  }

  @Test
  public void testLType()
  {
    runStar("lType.star");
  }

  @Test
  public void constructorExp()
  {
    runStar("constructorExp.star");
  }

  @Test
  public void pureStack()
  {
    runStar("pureStack.star");
  }

  @Test
  public void recordPattern()
  {
    runStar("recordPattern.star");
  }

  @Test
  public void tplAlias()
  {
    exception.expectMessage("tup expects 1 type arguments");
    runStar("tplAlias.star");
  }

  @Test
  public void tupleTypes()
  {
    runStar("tupleTypes.star");
  }

  @Test
  public void testDefault()
  {
    runStar("pmf.star");
  }

  @Test
  public void testUnivRef()
  {
    exception.expectMessage("may not quantify ref");

    runStar("univRef.star");
  }

  @Test
  public void testUnivProc()
  {
    runStar("univProc.star");
  }

  @Test
  public void testOpen()
  {
    runStar("openTest.star");
  }

  @Test
  public void testSubst()
  {
    runStar("encapSubstTest.star");
  }

  @Test
  public void testExistContracts()
  {
    runStar("graphing.star");
  }

  @Test
  public void testModuleContract()
  {
    runStar("moduleContracts.star");
  }
  
  @Test
  public void optionChaining()
  {
    runStar("optionChaining.star");
  }

  @Test
  public void mutualRec(){
    runStar("mutualFuns.star");
  }
}
