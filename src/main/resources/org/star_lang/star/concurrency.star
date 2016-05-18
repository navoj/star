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
import compute;
import task;
import cml;
private import strings;
private import base;
private import arithmetic;
private import actors;

-- support the form "wait for <rendezvous>"

#prefix("wait for",999);
#prefix("choose",980);
#prefix("wrap",910);
#prefix("guard",900);
#prefix("incoming",900);
#prefix("put",910);
#infix("into",850);
#prefix("timeout",700);
#prefix("at",700);
#prefix("always",700);

#prefix("receive",930);

#choose ?E :: expression :- E::rendezvous ## {
  # ?L or ?R :: rendezvous :- L::rendezvous :& R::rendezvous;
  # (?L) :: rendezvous :- L::rendezvous;
  # always ?E :: rendezvous :- E::expression;
  # never :: rendezvous;
  # timeout ?T :: rendezvous :- T::expression;
  # at ?T :: rendezvous :- T::expression;
  # choose ?E :: rendezvous :- E::rendezvous;
  # incoming ?Ch :: rendezvous :- Ch::expression;
  # put ?M on ?Ch :: rendezvous :- M::expression :& Ch::expression;
  # wrap ?R in ?P -> ?T :: rendezvous :- P::pattern :& R::rendezvous :& T::expression;
  # wrap ?R in ?T :: rendezvous :- R::rendezvous :& T::expression;
  # guard ?R :: rendezvous :- R::expression;
  # ?R :: rendezvous :- R::expression;
};

#wait for ?E :: expression :- choose E :: expression;

#choose ?Rendez ==> rEnder(Rendez) ## {
  #fun rEnder(<|?L or ?R|>) is <|_choose2(?rEnder(L),?rEnder(R))|>
    |  rEnder(<|(?R)|>) is rEnder(R)
    |  rEnder(<|always ?X|>) is <|alwaysRv(?X)|>
    |  rEnder(<|never|>) is <|neverRv|>
    |  rEnder(<|timeout ?T|>) is <|timeoutRv(?T)|>
    |  rEnder(<|at ?T|>) is <|atDateRv(?T)|>
    |  rEnder(<|choose ?R|>) is <|rEnder(?R)|>
    |  rEnder(<|incoming ?Ch|>) is <|recvRv(?Ch)|>
    |  rEnder(<|put ?M on ?Ch|>) is <|sendRv(?Ch,?M)|>
    |  rEnder(<|wrap ?R in ?P->?T|>) is <|wrapRv(?rEnder(R),(?P) => ?T)|>
    |  rEnder(<|wrap ?R in ?F|>) is <|wrapRv(?rEnder(R),?F)|>
    |  rEnder(<|guard ?R|>) is <|guardRv(?R)|>
    |  rEnder(E) is E;
};

#wait for ?R ==> await(choose R);

type saNotifyFun of %t is alias of ((%t)=>()) => rendezvous of ();
type saRequestFun of %t is alias of (for all %r such that ((%t)=>%r) => rendezvous of %r);
  
type concActor of %t is conAct0r(saNotifyFun of %t,saRequestFun of %t);

-- performing a speech action on a concActor involves sending the speech function
-- to the underlying background server task and waiting for a reply.
  
implementation speech over concActor of %t determines (%t,task) is {
  fun _query(conAct0r(_,SAfun),Qf,_,_) is await(SAfun(Qf));
  fun _request(conAct0r(_,SAfun),Qf,_,_) is await(SAfun(Qf));
  fun _notify(conAct0r(Notifyfun,_),Np) is await(Notifyfun(Np));
};

#token("concurrent actor");

#concurrent actor{?Defs} :: expression :- Defs ;* statement;
  
#concurrent actor {?Defs} ==> actorHead(actorTheta(Defs));

-- convenience to allow type declaration concurrent actor of {}
#concurrent actor of ?T :: typeExpression :- T;*typeExpression;
#concurrent actor of ?T ==> concActor of T;

-- The trRequest constructor exposes the core handler function for concurrent actors 
private type tractorSa is trRequest{
    chnlType has kind type;
    queryFun has type ()=>chnlType;
    chnl has type channel of chnlType}
  or trNotify(()=>(),channel of ());
 
fun actorHead(Defs) is let{
  def actorChnl is channel();

  -- standard speech action processing function    
  fun speechFun(QF) is valof{
    def ReplyChnl is channel();
    
    -- send the request to the actor's server task
    ignore background task { perform await(sendRv(actorChnl,trRequest{ type %t counts as chnlType; fun queryFun() is QF(Defs); def chnl is ReplyChnl}))};
     
    valis recvRv(ReplyChnl);
  };
  
  fun notifyFun(QF) is valof{
     def ReplyChnl is channel();
    
    -- send the notify to the actor's server task
    ignore background task { perform await(sendRv(actorChnl,trNotify((() do QF(Defs)),ReplyChnl)))};
     
    valis choose incoming ReplyChnl;
  };
  
  fun loop() is task{
    while true do{
      switch valof recv(actorChnl) in {
        case trRequest{queryFun = QF; chnl=RepChnl} do {
          perform wait for put QF() on RepChnl; 
        }
        case trNotify(QF,RepChnl) do {
          perform wait for put () on RepChnl; -- reply immediately, before executing the notify itself 
          QF();
        }
      };
    }
  };
      
  { ignore background loop() };
} in conAct0r(notifyFun,speechFun);
