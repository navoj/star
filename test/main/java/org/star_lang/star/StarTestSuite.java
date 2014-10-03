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
