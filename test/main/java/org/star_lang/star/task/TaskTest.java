package org.star_lang.star.task;

import org.junit.Test;
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

public class TaskTest extends SRTest
{
  public TaskTest()
  {
    super(TaskTest.class);
  }

  @Test
  public void testSimplestTask()
  {
    runStar("simplestTask.star");
  }

  @Test
  public void testBasicPerform()
  {
    runStar("basicPerform.star");
  }

  @Test
  public void testTaskUtils()
  {
    runStar("taskUtilsTest.star");
  }

  @Test
  public void testTaskExp()
  {
    runStar("taskExpTest.star");
  }

  @Test
  public void testAbortTaskExp()
  {
    runStar("abortTask.star");
  }

  @Test
  public void testTaskPerformance()
  {
    runStar("taskPerformanceTest.star");
  }

  @Test
  public void testMiniTestSuitExp()
  {
    runStar("miniTestSuite.star");
  }

  @Test
  public void testParMap()
  {
    runStar("parmap.star");
  }

  @Test
  public void testTaskRing()
  {
    runStar("ring.star");
  }

  @Test
  public void testActionExp()
  {
    runStar("actiontest.star");
  }

  @Test
  public void testWorkerQ()
  {
    runStar("workers.star");
  }
}
