package org.star_lang.star.actors;

import org.junit.Test;
import org.star_lang.star.compiler.SRTest;

/**
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
