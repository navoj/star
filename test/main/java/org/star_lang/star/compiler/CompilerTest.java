package org.star_lang.star.compiler;

import org.junit.Test;

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
public class CompilerTest extends SRTest
{
  public CompilerTest()
  {
    super(CompilerTest.class);
  }

  @Test
  public void testAlias()
  {
    runStar("typealias.star");
  }

  @Test
  public void testRecursiveAlias()
  {
    runStar("recalias.star");
  }

  @Test
  public void testAssignments()
  {
    runStar("blackWhite.star");
  }

  @Test
  public void testAny()
  {
    runStar("anytest.star");
  }

  @Test
  public void testDefaults()
  {
    runStar("defaults.star");
  }

  @Test
  public void testActionRules()
  {
    runStar("actionRules.star");
  }

  @Test
  public void testFunctions()
  {
    runStar("functions.star");
  }

  @Test
  public void testFree()
  {
    runStar("freevars.star");
  }

  @Test
  public void testDouble()
  {
    runStar("doubler.star");
  }

  @Test
  public void testDefValues()
  {
    runStar("defvalues.star");
  }

  @Test
  public void testLetFunBug()
  {
    runStar("letfunbug.star");
  }

  @Test
  public void testFirstBug()
  {
    runStar("firstTest.star");
  }

  @Test
  public void testKeith()
  {
    runStar("keith.star");
  }

  @Test
  public void testZebra()
  {
    runStar("zebra.star");
  }

  @Test
  public void testFooChoice()
  {
    runStar("foo.star");
  }

  @Test
  public void testWhile()
  {
    runStar("whileTest.star");
  }

  @Test
  public void testAggPattern()
  {
    runStar("gpslocator.star");
  }

  @Test
  public void testRecordTypeInference()
  {
    runStar("infer.star");
  }

  @Test
  public void testPatternAbstraction()
  {
    runStar("ptnAb.star");
  }

  @Test
  public void testErastosthenes()
  {
    runStar("eras.star");
  }

  @Test
  public void testQuoted()
  {
    runStar("quoting.star");
  }

  @Test
  public void testPersons()
  {
    runStar("persons.star");
  }

  @Test
  public void testFreeBug()
  {
    runStar("freebug.star");
  }

  @Test
  public void testEmptyPkg()
  {
    runStar("emptyPkg.star");
  }

  @Test
  public void testLocalActions()
  {
    runStar("localActions.star");
  }

  @Test
  public void testLogical()
  {
    runStar("logicalTest.star");
  }

  @Test
  public void testComplexConditions()
  {
    runStar("complexCond.star");
  }
}
