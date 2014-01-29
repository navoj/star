package org.star_lang.star.task;

import org.junit.Test;
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
  public void testTask()
  {
    runStar("taskTest.star");
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
}
