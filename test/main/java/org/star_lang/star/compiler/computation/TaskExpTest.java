package org.star_lang.star.compiler.computation;

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
public class TaskExpTest extends SRTest
{
  public TaskExpTest()
  {
    super(TaskExpTest.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testBasicTask()
  {
    runStar("basicTask.star");
  }

  @Test
  public void testSimpleCombine()
  {
    runStar("simpleCombine.star");
  }

  @Test
  public void testSubTask()
  {
    runStar("subTask.star");
  }

  @Test
  public void testInjection()
  {
    runStar("injectTest.star");
  }

  @Test
  public void caseTest()
  {
    runStar("caseTask.star");
  }

  @Test
  public void testConditionalTask()
  {
    runStar("condtask.star");
  }

  @Test
  public void testForLoopTask()
  {
    runStar("loopingTask.star");
  }

  @Test
  public void testWhileTask()
  {
    runStar("whileTask.star");
  }

  @Test
  public void testWhileSearchTask()
  {
    runStar("whileSearch.star");
  }

  @Test
  public void testTaskCond()
  {
    runStar("taskCondTest.star");
  }

  @Test
  public void testCondExpTask()
  {
    runStar("condexptask.star");
  }

  @Test
  public void testMaybe()
  {
    runStar("maybeTest.star");
  }

  @Test
  public void testPerform()
  {
    runStar("performTest.star");
  }

  @Test
  public void testValofValis()
  {
    runStar("sendOrder.star");
  }

  @Test
  public void testTypeDecl()
  {
    runStar("typeDecl.star");
  }

  @Test
  public void testTypeBug()
  {
    exception.expectMessage("task not equal to rendezvous");
    runStar("typeBug.star");
  }

  @Test
  public void testFixedException()
  {
    runStar("fixedException.star");
  }
}
