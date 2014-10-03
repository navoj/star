package org.star_lang.star.volunteer;

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
public class VolunteerTest extends SRTest
{
  @Rule
  public ExpectedException exception = ExpectedException.none().handleAssertionErrors();

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
