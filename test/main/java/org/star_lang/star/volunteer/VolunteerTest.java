package org.star_lang.star.volunteer;

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
public class VolunteerTest extends SRTest
{
  @Rule
  public ExpectedException exception = ExpectedException.none();

  public VolunteerTest()
  {
    super(VolunteerTest.class);
  }

  @Test
  public void testVolunteeer()
  {
    runStar("voltest.star");
  }

  @Test
  public void testSimpleVolunteeer()
  {
    runStar("voltest2.star");
  }

  @Test
  public void testSimple3Volunteeer()
  {
    runStar("voltest3.star");
  }

  @Test
  public void testTwoWay()
  {
    runStar("voltwoway.star");
  }

  @Test
  public void testSimpleRequestVolunteeer()
  {
    runStar("volrequest.star");
  }

  @Test
  public void test2ArgRequestVolunteeer()
  {
    runStar("volrequest2.star");
  }

  @Test
  public void test3ArgRequestVolunteeer()
  {
    runStar("volrequest3.star");
  }

  @Test
  public void testVolQuery()
  {
    runStar("volquery.star");
  }

  @Test
  public void testTwoWayRequestVolunteeer()
  {
    runStar("voltwowayrequest.star");
  }

  @Test
  public void testTwoWayRequestPort()
  {
    runStar("twowayporttest.star");
  }

  @Test
  public void testTwoWayRequestVolunteeer2()
  {
    runStar("voltwowayrequest2.star");
  }

  @Test
  public void testOneWayRequestPort()
  {
    runStar("onewayPortTest.star");
  }

  @Test
  public void testTwoWayQuery()
  {
    runStar("voltwowayquery.star");
  }

  @Test
  public void testAllQuery()
  {
    runStar("volallquery.star");
  }

  @Test
  public void testAllQueryPorts()
  {
    runStar("allqueryports.star");
  }

  @Test
  public void testTwoWayMulti()
  {
    exception.expectMessage("responded to by multiple ports");

    runStar("voltwowaymulti.star");
  }

  @Test
  public void testBadVolunteer()
  {
    runStar("badreqvol.star");
  }

  @Test
  public void testMultiPort()
  {
    runStar("multiRequestPort.star");
  }

  @Test
  public void testMultiNotifyPort()
  {
    runStar("multiNotifyPort.star");
  }

  @Test
  public void testEmpty()
  {
    runStar("emptyvol.star");
  }

  @Test
  public void testQueryPorts()
  {
    runStar("queryRelationsPorts.star");
  }

  @Test
  public void testQueryVol()
  {
    runStar("voltestrels.star");
  }

  @Test
  public void testMultiReduceVol()
  {
    runStar("voltestreduce.star");
  }

  @Test
  public void testTwoWayFunctions()
  {
    runStar("queryfuntest.star");
  }
}
