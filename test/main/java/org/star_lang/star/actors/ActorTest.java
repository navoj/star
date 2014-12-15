package org.star_lang.star.actors;

import org.junit.Ignore;
import org.junit.Test;
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
public class ActorTest extends SRTest
{
  public ActorTest()
  {
    super(ActorTest.class);
  }

  @Test
  public void testChatty()
  {
    runStar("chatty.star");
  }

  @Test
  public void testAct0rs()
  {
    runStar("act0rs.star");
  }

  @Test
  public void testNotifyBlast()
  {
    runStar("notifyBlast.star");
  }

  @Test
  public void actorBank()
  {
    runStar("actorbank.star");
  }

  @Test
  public void testFactorActor()
  {
    runStar("actorFactorial.star");
  }

  @Test
  public void testActorQueries()
  {
    runStar("actorqueries.star");
  }

  @Test
  public void testActorAssignment()
  {
    runStar("assigntest.star");
  }

  @Test
  public void testOrdering()
  {
    runStar("picoSequence.star");
  }

  @Test
  public void testRequests()
  {
    runStar("picoRequest.star");
  }

  @Test
  public void testActorTypeInference()
  {
    runStar("peopleAgents.star");
  }

  @Test
  @Ignore
  public void testActorSendFun()
  {
    runStar("actorSendFun.star");
  }

  @Test
  public void testStocker()
  {
    runStar("stocker.star");
  }

  @Test
  public void testClashingNames()
  {
    runStar("clashingNames.star");
  }

  @Test
  public void testFreeGreek()
  {
    runStar("freeGreek.star");
  }

  @Test
  public void testMemoBug()
  {
    runStar("memoBug.star");
  }

  @Test
  public void testIntroTp()
  {
    runStar("introTp.star");
  }

  @Test
  public void testActorUpdate()
  {
    runStar("actorupdate.star");
  }

  @Test
  public void testLetActor()
  {
    runStar("actorLets.star");
  }

  @Test
  public void anonFunInActor()
  {
    runStar("anonFunInActor.star");
  }

  @Test
  public void anonActorCoerce()
  {
    runStar("actorCoerce.star");
  }

  @Test
  public void testBank()
  {
    runStar("bank.star");
  }

  @Test
  public void testTickTackToe()
  {
    runStar("tickTackToe.star");
  }

  @Test
  public void testRouteActor()
  {
    runStar("routeActor.star");
  }

  @Test
  public void testSendToActor()
  {
    runStar("sendToConc.star");
  }
}
