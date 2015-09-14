package org.star_lang.star;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.star_lang.star.compiler.CompilerTestSuite;
import org.star_lang.star.data.DataTestSuite;
import org.star_lang.star.grammar.GrammarTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ DataTestSuite.class, GrammarTestSuite.class, CompilerTestSuite.class })
public class StarTestSuite
{
  public static void main(String args[])
  {
    Result result = JUnitCore.runClasses(StarTestSuite.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
    System.out.println("Test took " + result.getRunTime() + " milliseconds");
  }
}
