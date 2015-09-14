package org.star_lang.star.compiler.macro;

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
public class MacroTests extends SRTest
{
  public MacroTests()
  {
    super(MacroTests.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

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
