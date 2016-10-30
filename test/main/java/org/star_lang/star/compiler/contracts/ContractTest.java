package org.star_lang.star.compiler.contracts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.star_lang.star.compiler.SRTest;

public class ContractTest extends SRTest
{

  public ContractTest()
  {
    super(ContractTest.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testNonFunContracts()
  {
    runStar("nonfuncon.star");
  }

  @Test
  public void testSimpleDepContracts()
  {
    runStar("dependcon.star");
  }

  @Test
  public void testPlus()
  {
    runStar("testplus.star");
  }

  @Test
  public void testFunnyMinus()
  {
    runStar("minus.star");
  }

  @Test
  public void testPlusCon()
  {
    runStar("testPlusCon.star");
  }

  @Test
  public void testDepContracts()
  {
    runStar("dependentcontracts.star");
  }

  @Test
  public void testDepType()
  {
    runStar("dependtype.star");
  }

  @Test
  public void testComplexCons()
  {
    runStar("complexCons.star");
  }

  @Test
  public void testRandom()
  {
    runStar("genericcontracts.star");
  }

  @Test
  public void testOverride()
  {
    runStar("testOverride2.star");
  }

  @Test
  public void testNoResolve()
  {
    exception.expectMessage("unresolved variable: loveIsAllAround");
    runStar("lovetest.star");
  }

  @Test
  public void testInnerResolve()
  {
    // tests occur check actually.
    runStar("MyMap.star");
  }

  @Test
  public void testUnravel()
  {
    runStar("unravel.star");
  }

  @Test
  public void testContractTypeCollision()
  {
    exception.expectMessage("token is not legal here");

    runStar("tokenContract.star");
  }

  @Test
  public void testCompContract()
  {
    runStar("tupleContracts.star");
  }

  @Test
  public void testEntailment()
  {
    runStar("orderedTest.star");
  }

  @Test
  public void badFour()
  {
    exception.expectMessage("four over integer not known to be implemented");

    runStar("badFour.star");
  }
}
