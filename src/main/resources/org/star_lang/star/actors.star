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

contract speech over t determines (u,a) where execution over a is {
  _query has type for all s such that (t,(u)=>s,()=>quoted,()=>dictionary of (string,quoted))=>a of s;
  _request has type (t,(u)=>(),()=>quoted,()=>dictionary of (string,quoted)) => a of ();
  _notify has type (t,(u)=>()) => a of ();
};

implementation speech over actor of %t determines (%t,action) is {
  fun _query(act0r(Ac),Qf,_,_) is action { valis Qf(Ac)};
  fun _request(act0r(Ac),Rf,_,_) is action{ Rf(Ac) };
  fun _notify(act0r(Ac),Np) is action{ Np(Ac) };
};

type actor of %t is act0r(%t);

type occurrence of %t is alias of action(%t);

#prefix((notify),1100);
#prefix((query),1000);
#prefix((request),1050);

-- _notify speech action
# notify ?Ag with ?Exp on identifier :: action :- Exp::expression :& Ag::expression;
# notify ?A with ?E on ?C ==> perform _notify(A,((#$"XX") do C(E) using #$"XX"'s C));

-- _query speech action
# query ?Ag with ?Exp :: expression :- Ag::agentExpression :& Exp::expression;
# ?A 's ?S :: agentExpression :- A::expression :& S :: names;
# ?A :: agentExpression :- A::expression;

# freeVarMap(?Exp) ==> freeVarMap(Exp,());
# freeVarMap(?Exp,?XX) ==> #*formHash(__find_free(Exp,XX,comma,())) ## {
  #formHash(()) ==> dictionary of [];
  #formHash(?E) ==> dictionary of [hashEntries(E)];
  #hashEntries(comma(?V,())) ==> $$V->#(V as quoted)#;
  #hashEntries(comma(?V,?T)) ==> #($$V->#(V as quoted)#,hashEntries(T))#;
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

#query ?A's ?Ex with ?E ==> valof _query(A,(#$"XX") => E using #$"XX"'s Ex,() => quote(E),() => queryFree(E,Ex));
#query ?A with ?E ==> valof _query(A,((#$"XX") => let { open #$"XX" } in E),(() => quote(E)),(() => queryFree(E,())));

-- _request speech actions  
#request ?Ag to ?Act :: action :- Act::action :& Ag::agentExpression;

#request ?A to extend ?Tgt with ?Exp  ==>
        perform _request(A,((#$"XX") do {#(#$"XX")#.Tgt := _extend(#(#$"XX")#.Tgt,Exp)}),(() => <|extend ?Tgt with ?Exp|>),(() => freeVarMap(Exp)));
#request ?A to merge ?Tgt with ?Exp  ==>
        perform _request(A,((#$"XX") do {#(#$"XX")#.Tgt := _merge(#(#$"XX")#.Tgt,Exp)}),(() => <|merge ?Tgt with ?Exp|>),(() => freeVarMap(Exp)));
#request ?A to delete ?Ptn in ?Tgt  ==>
        perform _request(A,((#$"XX") do {#(#$"XX")#.Tgt := _delete(#(#$"XX")#.Tgt,(() from Ptn))}),(() => <|delete ?Ptn in ?Tgt|>),(() => dictionary of []));
#request ?A to update ?Ptn in ?Tgt with ?Exp  ==>
        perform _request(A,((#$"XX") do {#(#$"XX")#.Tgt := _update(#(#$"XX")#.Tgt,(() from Ptn), (Ptn) => Exp)}),(() => <|update ?Ptn in ?Tgt with ?Exp|>),() => freeVarMap(Exp,Ptn));

#request ?A's ?Ex to ?Act  ==> perform _request(A,((#$"XX") do {Act using #$"XX"'s Ex}),(() => quote(Act)),(() => freeVarMap(Act,Ex)));
#request ?A to ?Act ==> perform _request(A,((#$"XX") do {let{ open #$"XX" } in Act}),(() => quote(Act)),(() => freeVarMap(Act,())));

-- event rules
# on ?E do ?A :: statement :-  A :: action :& E::eventCondition ## {
  # ?P on identifier :: eventCondition:- P::pattern;
  # ?P on identifier where ?Cond :: eventCondition :- P::pattern :& Cond::condition;
};

-- manage the transformation of actors
#actor{?Defs} :: expression :- Defs ;* statement;

#actor {?Defs} ==> act0r(actorTheta(Defs));

#actorTheta(?ActorDefs) ==> {makeActorRules(ActorDefs)} ## {
  
  #fun makeActorRules(Defs) is let{
    fun collectRules(Rl matching <|on ?Evt do ?Act|>, (Theta, EventRules)) is (Theta,insertEventRule(EventRules,eventRule(Rl)))
     |  collectRules(<|?L;?R|>,Coll) is collectRules(R,collectRules(L,Coll))
     |  collectRules(Stmt,(Theta,Rules)) is (list of [Theta..,Stmt],Rules)

    fun insertEventRule(Rules,Rl matching (Ch,P,C,A)) where _index(Rules,Ch) matches some(chnnlRules) is _set_indexed(Rules,Ch,list of [chnnlRules..,Rl])
     |  insertEventRule(Rules,Rl matching (Ch,P,C,A)) is _set_indexed(Rules,Ch,list of [Rl])

    -- pick apart an event condition into individual pieces
    fun eventRule(<|on ?P on ?Ch where ?C do ?A|>) is (Ch,P,C,A)
     |  eventRule(<|on ?P on ?Ch do ?A|>) is (Ch,P,<|true|>,A)

    fun channelProc(Ch,Rules) is let{
      def eVar is _macro_gensym("evt");
    
      fun rlProc((_,P,C,A)) is let{
        def ecaName is _macro_gensym("eca");
      } in (<| prc #(?ecaName)# (?P) where ?C do ?A |>, <|#(?ecaName)# (?eVar) |>);

      fun makeRules(_empty()) is (<|{}|>,<|{}|>)
       |  makeRules(_pair(Rl,_empty())) is rlProc(Rl)
       |  makeRules(_pair(Rl,Ules)) is valof{
        def (Pr,Cl) is rlProc(Rl);
        def (D,C) is makeRules(Ules);
        valis (semi(D,Pr),semi(C,Cl))
      };
      
      fun semi(X,Y) is <| ?X ; ?Y |>;
      
      fun makeEcaProc((Dfs, Calls)) is <| prc #(?Ch)#(?eVar) do {?Calls} using { ?Dfs } |>;

      fun makeEca(list of [(_,P,C,A)]) is <| prc #(?Ch)#(?P) where ?C do ?A |>
       |  makeEca(Rls) is makeEcaProc(makeRules(Rls))
    } in makeEca(Rules);

    fun makeActorTheta((Theta,EvtRules)) is let{
      def eventRules is list of { all channelProc(Ch,Rules) where Ch->Rules in EvtRules}
    } in __wrapSemi(eventRules,__wrapSemi(Theta,<|{}|>));

  } in makeActorTheta(collectRules(Defs,(list of [], dictionary of [])));
}