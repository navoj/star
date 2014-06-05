package org.star_lang.star.compiler.contracts;

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
public class ContractTest extends SRTest
{

  public ContractTest()
  {
    super(ContractTest.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none().handleAssertionErrors();

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
