package org.star_lang.star.compiler.macro;

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
public class MacroTests extends SRTest
{
  public MacroTests()
  {
    super(MacroTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none().handleAssertionErrors();

  @Test
  public void testBasic()
  {
    runStar("macrotest.star");
  }

  @Test
  public void testColonMacro()
  {
    runStar("colonMacroTest.star");
  }

  @Test
  public void testDotSlash()
  {
    runStar("dotslash.star");
  }

  @Test
  public void testMacroLog()
  {
    runStar("macrolog.star");
  }

  @Test
  public void testMacroCatenate()
  {
    runStar("macrocatenate.star");
  }

  @Test
  public void testMacroStar()
  {
    runStar("starMacro.star");
  }

  @Test
  public void testMacroIntern()
  {
    runStar("macrointern.star");
  }

  @Test
  public void testMultiToken()
  {
    runStar("multiop.star");
  }

  @Test
  public void testActorGen()
  {
    runStar("actorgen.star");
  }

  @Test
  public void internTest()
  {
    runStar("internTest.star");
  }

  @Test
  public void genTest()
  {
    runStar("macroGen.star");
  }

  @Test
  public void testConceptMacros()
  {
    runStar("conceptMacros.star");
  }

  @Test
  public void testComplextLets()
  {
    runStar("complexMacroLets.star");
  }

  @Test
  public void testDequote()
  {
    runStar("dequoteTest.star");
  }

  @Test
  public void testBeginEnd()
  {
    runStar("pascal.star");
  }

  @Test
  public void testMiniRdf()
  {
    runStar("miniRdf.star");
  }

  @Test
  public void testLambda()
  {
    runStar("lambda.star");
  }

  @Test
  public void testWorkSheet()
  {
    runStar("workEx1.star");
  }

  @Test
  public void testApplyQuote()
  {
    runStar("applyQuote.star");
  }

  @Test
  public void testLazyConv()
  {
    runStar("trialLazy.star");
  }
}
