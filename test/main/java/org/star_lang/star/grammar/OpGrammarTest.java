package org.star_lang.star.grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.Before;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.grammar.OpGrammar;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.data.value.ResourceURI;

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
public class OpGrammarTest extends TestCase
{

  @Override
  @Before
  public void setUp() throws Exception
  {
  }

  // Test simple terms
  public void runTest(String lbl, String text)
  {
    try (Reader strReader = new StringReader(text)) {

      ErrorReport errors = new ErrorReport();
      OpGrammar parser = new OpGrammar(Operators.operatorRoot().copy(), errors);

      IAbstract term = parser.parse(ResourceURI.noUriEnum, strReader, null);
      System.out.println(lbl + " = " + term.toString());
      if (!errors.isErrorFree())
        fail("Parse error " + errors);
    } catch (IOException e) {
    }
  }

  // Negative test -- should fail
  public void negTest(String lbl, String text) throws IOException
  {
    try (Reader strReader = new StringReader(text)) {

      ErrorReport errors = new ErrorReport();
      OpGrammar parser = new OpGrammar(Operators.operatorRoot().copy(), errors);

      parser.parse(ResourceURI.noUriEnum, strReader, null);
      if (errors.isErrorFree())
        fail("[" + lbl + "] should have reported a parse error");
      else
        System.out.println("Expected parse error " + errors);
    }
  }

  public void testSimple()
  {
    runTest("Simple test 1", "12.3");
    runTest("Simple test 2", "f(12)");
    runTest("Simple test 3", "[1,2,a]");
    runTest("Simple test 4", "f(a)(b,c)");
    runTest("Simple test 4a", "f()(b,c)");
    runTest("Simple test 5", "(+)");
    runTest("Simple test 6", "{a=12}");
    runTest("Simple test 7", "{a=12,b=c,(c)=23.5f}");
    runTest("Simple test 8", "{a,b,c}");
    runTest("Simple test 9", "{}");
    runTest("Simple test 10", "A.{a}");
    runTest("Simple test 11", "{a=[12]}");
    runTest("Simple test 12", "[{a=12},{},{b=[]}]");
    runTest("Simple test 13", "L[23]");
    runTest("Simple test 14", "L[A:B]");
    runTest("Simple test 15", "L[1,2,A]");
  }

  public void testNegSimple() throws IOException
  {
    negTest("neg test 1", "12)");
    negTest("neg test 2", "(12");
    negTest("neg test 3", "[)");
    negTest("neg test 4", "(]");
  }

  public void testOperators()
  {
    runTest("Op test 1", "a+b*c");
    runTest("Op test 1", "a-b-c+d");
    runTest("InfPost", "a;b;");
    runTest("postTest", "?A; Action :- A::Action");
    runTest("onTest", "?X on ?Chnl order ?X on ?ResCh");
  }

  public void testBrackets()
  {
    runTest("br test 1", "a{}");
  }

  public void testSyntax()
  {
    runTest("fun 1",
        "append is package{\n  append(X1,Y1) is collect{\n    for E1 in X2 do\n  {\n elemis E2\n    };\n    valis Y2\n    }\n}");

    runTest("op test1", "package #infix((alpha),500); A alpha B; end");
    runTest("op test2", "package{ #infix((aleph),500); #pair((alpha),(omega),1000); alpha A aleph B omega; }");
  }

  public void testResource()
  {
    runTest("frag2", "Data[0].AN");
    runTest("frag3", "post Event at t1 to Destinations.(key)");
    runTest("frag4", "post Event at t2 to Destinations.key");
    runTest("frag5", "#(?F)#@#(?Arg)#");
  }

  public void testBad() throws IOException
  {
    negTest(
        "<bad>",
        "\nlklk\nl;\nl;l\nl;l;l;\nl;hello is package {\n  x has type string;\n  main() {\n    x is \"abc\";\n   logMsg(info, \"hello, $x\");\n}\nhello is package {\nx has type string;\nmain() {\n  x is \"abc\";\n    logMsg(info, \"hello, $x\");\n  }\n  hello is package {\n  x has type string;\n  main() {");
  }
}
