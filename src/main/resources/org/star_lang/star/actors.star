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
private import base;
private import strings;
private import compute;
private import validate;
private import folding;
private import sequences;
private import iterable;
private import arrays;
private import maps;
private import macrosupport;

contract speech over %t determines (%u,%%a) where execution over %%a is {
  _query has type for all %s such that (%t,(%u)=>%s,()=>quoted,()=>dictionary of (string,quoted))=>%%a of %s;
  _request has type (%t,(%u)=>(),()=>quoted,()=>dictionary of (string,quoted)) => %%a of ();
  _notify has type (%t,(%u)=>()) => %%a of ();
};

implementation speech over actor of %t determines (%t,action) is {
  _query(act0r(Ac),Qf,_,_) is action { valis Qf(Ac)};
  _query(nonActor,_,_,_) is action{ raise "cannot query nonActor"};
  _request(act0r(Ac),Rf,_,_) is action{ Rf(Ac) };
  _request(nonActor,_,_,_) is action{};
  _notify(act0r(Ac),Np) is action{ Np(Ac) };
  _notify(nonActor,_) is action{};
};

type actor of %t is act0r(%t) or nonActor;

type occurrence of %t is alias of action(%t);

#prefix((notify),1100);
#prefix((query),1000);
#prefix((request),1050);

-- _notify speech action
# notify ?Ag with ?Exp on identifier :: action :- Exp::expression :& Ag::expression;
# notify ?A with ?E on ?C ==> perform _notify(A,(procedure(#$"XX") do C(E) using #$"XX"'s C));

-- _query speech action
# query ?Ag with ?Exp :: expression :- Ag::agentExpression :& Exp::expression;
# ?A 's ?S :: agentExpression :- A::expression :& S :: names;
# ?A :: agentExpression :- A::expression;

# freeVarMap(?Exp) ==> freeVarMap(Exp,());
# freeVarMap(?Exp,?XX) ==> #*formHash(__find_free(Exp,XX,comma,())) ## {
  #formHash(()) ==> dictionary of {};
  #formHash(?E) ==> dictionary of {hashEntries(E)};
  #hashEntries(comma(?V,())) ==> $$V->#(V as quoted)#;
  #hashEntries(comma(?V,?T)) ==> #($$V->#(V as quoted)#;hashEntries(T))#;
};

#queryFree(?Qq,?Excl) ==> freeVarMap(Qq,#*queryDefined(Qq,Excl)) ## {
  #queryDefined(?L order by ?C,?Ex) ==> queryDefined(L,Ex);
  #queryDefined(unique ?C of ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(unique ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(all ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(#(anyof ?E where ?Q default ?D)#,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(anyof ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(#(any of ?E where ?Q default ?D)#,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(any of ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(?C of ?E where ?Q,?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(?T of ?C of {?E where ?Q},?Ex) ==> fold(Q,freePtn,Ex);
  #queryDefined(?T of {?Q},?Ex) ==> queryDefined(Q,Ex);
  #queryDefined(?Q,?Ex) ==> Ex;

  #fold(?L and ?R,?F,?I) ==> fold(L,F,#*fold(R,F,I));
  #fold(?L or ?R,?F,?I) ==> fold(L,F,#*fold(R,F,I));
  #fold(?L implies ?R,?F,?I)  ==> fold(L,F,#*fold(R,F,I));
  #fold(?L otherwise ?R,?F,?I) ==> fold(L,F,#*fold(R,F,I));
  #fold(not ?N,?F,?I) ==> fold(N,F,I);
  #fold(?T,?F,?I) ==> F(T,I);

  #freePtn(?P in ?C,?I) ==> __find_free(P,I,comma,I);
  #freePtn(?V matches ?P,?I) ==> __find_free(P,I,comma,I);
  #freePtn(?X,?I) ==> I;
}

#query ?A's ?Ex with ?E ==> valof _query(A,(function(#$"XX") is E using #$"XX"'s Ex),(function() is quote(E)),(function() is queryFree(E,Ex)));
#query ?A with ?E ==> valof _query(A,(function(#$"XX") is let { open #$"XX" } in E),(function() is quote(E)),(function() is queryFree(E,())));

-- _request speech actions  
#request ?Ag to ?Act :: action :- Act::action :& Ag::agentExpression;

#request ?A to extend ?Tgt with ?Exp  ==>
        perform _request(A,(procedure(#$"XX") do {#(#$"XX")#.Tgt := _extend(#(#$"XX")#.Tgt,Exp)}),(function() is <|extend ?Tgt with ?Exp|>),(function() is freeVarMap(Exp)));
#request ?A to merge ?Tgt with ?Exp  ==>
        perform _request(A,(procedure(#$"XX") do {#(#$"XX")#.Tgt := _merge(#(#$"XX")#.Tgt,Exp)}),(function() is <|merge ?Tgt with ?Exp|>),(function() is freeVarMap(Exp)));
#request ?A to delete ?Ptn in ?Tgt  ==>
        perform _request(A,(procedure(#$"XX") do {#(#$"XX")#.Tgt := _delete(#(#$"XX")#.Tgt,(pattern() from Ptn))}),(function() is <|delete ?Ptn in ?Tgt|>),(function() is dictionary of {}));
#request ?A to update ?Ptn in ?Tgt with ?Exp  ==>
        perform _request(A,(procedure(#$"XX") do {#(#$"XX")#.Tgt := _update(#(#$"XX")#.Tgt,(pattern() from Ptn), (function(Ptn) is Exp))}),(function() is <|update ?Ptn in ?Tgt with ?Exp|>),(function() is freeVarMap(Exp,Ptn)));

#request ?A's ?Ex to ?Act  ==> perform _request(A,(procedure(#$"XX") do {Act using #$"XX"'s Ex}),(function() is quote(Act)),(function() is freeVarMap(Act,Ex)));
#request ?A to ?Act ==> perform _request(A,(procedure(#$"XX") do {let{ open #$"XX" } in Act}),(function() is quote(Act)),(function() is freeVarMap(Act,())));

-- event rules
# on ?E do ?A :: statement :-  A :: action :& E::eventCondition ## {
  # ?P on identifier :: eventCondition:- P::pattern;
  # ?P on identifier where ?Cond :: eventCondition :- P::pattern :& Cond::condition;
};

-- manage the transformation of actors
#actor{?Defs} :: expression :- Defs ;* statement;

#actor {?Defs} ==> act0r(actorTheta(Defs));

#actorTheta(?ActorDefs) ==> {makeActorRules(ActorDefs)} ## {
  
  #makeActorRules(Defs) is let{
    collectRules(Rl matching <|on ?Evt do ?Act|>, (Theta, EventRules)) is (Theta,insertEventRule(EventRules,eventRule(Rl)));
    collectRules(<|?L;?R|>,Coll) is collectRules(R,collectRules(L,Coll));
    collectRules(Stmt,(Theta,Rules)) is (list of {Theta..;Stmt},Rules);

    insertEventRule(Rules,Rl matching (Ch,P,C,A)) where _index(Rules,Ch) matches some(chnnlRules) is _set_indexed(Rules,Ch,list of {chnnlRules..;Rl});
    insertEventRule(Rules,Rl matching (Ch,P,C,A)) is _set_indexed(Rules,Ch,list of {Rl});

    -- pick apart an event condition into individual pieces
    eventRule(<|on ?P on ?Ch where ?C do ?A|>) is (Ch,P,C,A);
    eventRule(<|on ?P on ?Ch do ?A|>) is (Ch,P,<|true|>,A);

    channelProc(Ch,Rules) is let{
      eVar is _macro_gensym("evt");
    
      rlProc((_,P,C,A)) is let{
        ecaName is _macro_gensym("eca");
      } in (<| #(?ecaName)# (?P) where ?C do ?A |>, <|#(?ecaName)# (?eVar) |>);

      makeRules(_empty()) is (<|{}|>,<|{}|>);
      makeRules(_pair(Rl,_empty())) is rlProc(Rl);
      makeRules(_pair(Rl,Ules)) is valof{
        (Pr,Cl) is rlProc(Rl);
        (D,C) is makeRules(Ules);
        valis (semi(D,Pr),semi(C,Cl))
      };
      
      semi(X,Y) is <| ?X ; ?Y |>;
      
      makeEcaProc((Dfs, Calls)) is <| #(?Ch)#(?eVar) do {?Calls} using { ?Dfs } |>;

      makeEca(list of {(_,P,C,A)}) is <| #(?Ch)#(?P) where ?C do ?A |>;
      makeEca(Rls) is makeEcaProc(makeRules(Rls));
    } in makeEca(Rules);

    makeActorTheta((Theta,EvtRules)) is let{
      eventRules is list of { all channelProc(Ch,Rules) where Ch->Rules in EvtRules};
    } in __wrapSemi(eventRules,__wrapSemi(Theta,<|{}|>));

  } in makeActorTheta(collectRules(Defs,(list of {}, dictionary of {})));
}