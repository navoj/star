package org.star_lang.star.compiler.computation;

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

public class TaskExpTest extends SRTest {
  public TaskExpTest() {
    super(TaskExpTest.class);
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void testBasicTask() {
    runStar("basicTask.star");
  }

  @Test
  public void testSimpleCombine() {
    runStar("simpleCombine.star");
  }

  @Test
  public void testSubTask() {
    runStar("subTask.star");
  }

  @Test
  public void testInjection() {
    runStar("injectTest.star");
  }

  @Test
  public void caseTest() {
    runStar("caseTask.star");
  }

  @Test
  public void condValisTask() {
    runStar("condValisTask.star");
  }

  @Test
  public void forloopTask() {
    runStar("forloopTask.star");
  }

  @Test
  public void testConditionalTask() {
    runStar("condtask.star");
  }

  @Test
  public void testForLoopTask() {
    runStar("loopingTask.star");
  }

  @Test
  public void testWhileTask() {
    runStar("whileTask.star");
  }

  @Test
  public void testWhileSearchTask() {
    runStar("whileSearch.star");
  }

  @Test
  public void testTaskCond() {
    runStar("taskCondTest.star");
  }

  @Test
  public void testCondExpTask() {
    runStar("condexptask.star");
  }

  @Test
  public void testPerform() {
    runStar("performTest.star");
  }

  @Test
  public void testValofValis() {
    runStar("sendOrder.star");
  }

  @Test
  public void testTypeDecl() {
    runStar("typeDecl.star");
  }

  @Test
  public void testTypeBug() {
    exception.expectMessage("task not consistent with rendezvous");
    runStar("typeBug.star");
  }

  @Test
  public void letComp(){
    runStar("letcomp.star");
  }

  @Test
  public void testFixedException() {
    runStar("fixedException.star");
  }
}
