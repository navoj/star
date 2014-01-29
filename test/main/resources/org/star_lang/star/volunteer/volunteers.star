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
volunteers is package{
  import ports;
 
 -- Handle volunteers between ports.
 -- Input consists of a set of connection specifications
 
 -- port(name,schema)
 -- connect(name,name,volunteer)
 
 #prefix((volunteer),1200);
 #force(infix((as),945)); -- we are redefining this operator
 
 /*
 Assuming an originating port called Ao connected to two responding ports:

 input will take the form:

import volunteers;
Name is connections{
     originate(Ao,{DATA has type stream of Person; ACT has type (Person)=>(); R has type relation of ((Person,string))});
     respond(Br,{INP has type stream of Person});
     respond(Cr,{DATA has type stream of Person; ACT has type (Person)=>(); R has type relation of ((Person,string))});
     connect(Ao,Br,(X on DATA as X on INP));
     connect(Ao,Cr,(query X as X));
   }
   
   results in a function to be attached to Ao's port:
    
   Name is package{
      import speechContract;
      import portType;
      
      -- This is actually in portType
      type port of %schema is port{
        _notify has type ((%schema)=>())=>();
        _request has type action((%schema)=>(),()=>quoted,()=>map of (string,any));
        _query has type  for all %a such that ((%schema)=>%a,()=>quoted,()=>map of (string,any))=>%a;
      };
      
      connectAo(Br,Cr) is port{
        private pA is { DATA(X) do { Br._notify((procedure(SS) do SS.INP(X)));
                                     Cr._notify((procedure(SS) do SS.DATA(X)))}
                      };
        
        _notify(Nf) do Nf(pA);
        _query(Qf,QQ,QFr) is Cr._query(Qf,QQ,QFr);
        _request(Rf,RQ,RFr) do Cr._request(Rf,RQ,RFr); 
      }
    }
 */

  #?I is connections{?sts} :: statement :- sts;*connectionSpec ## {
    originate(?i,{?schema}) :: connectionSpec :- i::id :& schema;*connectTypeAnnotation;
    respond(?i,{?schema}) :: connectionSpec :- i::id :& schema;*connectTypeAnnotation;
    connect(?o,?r,?v) :: connectionSpec;
    
    # ?Id has type ?Tp :: connectTypeAnnotation :- Id::id :& Tp::legalConnectType;
    
    # (?T) :: legalConnectType :- T::legalConnectType;
    # relation of ?T :: legalConnectType :- T::typeExpression;
    # stream of ?T :: legalConnectType :- T::typeExpression;
    # hash of (?K,?V) :: legalConnectType :- K::typeExpression :& V::typeExpression;
    # map of (?K,?V) :: legalConnectType :- K::typeExpression :& V::typeExpression;
    # #(tuple?A)# => ?R :: legalConnectType :- A:*typeExpression :& R::typeExpression;
    # ?A <= ?P :: legalConnectType :- A::typeExpression :& P::typeExpression;
    # ?A => ?P :: legalConnectType :- A::typeExpression :& P::typeExpression;
    # for all ?TV such that ?Tp :: legalConnectType :- TV::typeVars :& Tp::legalConnectType;
    # action#@#(tuple?Arg)# :: legalConnectType :- Arg:*typeExpression;
    # ref ?Tp :: legalConnectType :- Tp :: legalConnectType;
  
    # ?T :: legalConnectType :- error("$T not permitted within a port schema");
  };
  
  #?I is connections{?ConnSpecs} ==> I is package { connections(ConnSpecs,ConnSpecs,()) } ## {
    #connections(#(?L;?R)#,?Specs,?Conns) ==> connections(R,Specs,connections(L,Specs,Conns));
    #connections(originate(?OP,{?Schema}),?Specs,?Conns) ==> glom(#*connection(OP,Schema,Specs),Conns);
    #connections(?St,?Specs,?Conns) ==> Conns;
    
    #connection(?OP,?Schema,?Specs) ==> 
	#(
	 connect#+OP has type #<connectedPortTypes(connectedPorts(OP,Specs,()),Specs)>#=>portType(OP,Specs,());
	 connect#+OP#@#<connectedPorts(OP,Specs,())># is
	     unwrapSAs(#$"lVar",
		   mergeSA(processNotifies(#$"lVar",Schema,OP,Specs),
			 mergeSA(processRequests(#$"lVar",Schema,OP,Specs),
			       processQueries(#$"lVar",Schema,OP,Specs))))
	 )# ## {
	  #unwrapSAs(?lVar, (?Outer, ())) ==> port{Outer};
	  #unwrapSAs(?lVar, (?Outer, ?Local)) ==> 
	      let{ 
		lVar has type {adjustSchema(Schema)}; 
		lVar is {#*generateFromSchema(Local,Schema)}
	      } in port{Outer};
	  
	  #adjustSchema(?S) ==> S;
	  
	  #mergeSA( (?L1,?R1), (?L2,?R2)) ==> (mrge(L1,L2),mrge(R1,R2)) ## {
	    #mrge((),?X) ==> X;
	    #mrge(?X,()) ==> X;
	    #mrge(?X,?Y) ==> glom(X,Y)
	  };
	  #mergeSA( (), ?R) ==> R;
	  #mergeSA( ?L, ()) ==> L;
	 };
    
    #connectedPorts(?OP,connect(?OP,?Res,?V),?Ports./?Res) ==> Ports;
    #connectedPorts(?OP,connect(?OP,?Res,?V),?Ports) ==> (Res,Ports);
    #connectedPorts(?OP,#(?L;?R)#,?Ports) ==> connectedPorts(OP,L,connectedPorts(OP,R,Ports));
    #connectedPorts(?OP,?X,?Ports) ==> Ports;
    
    #connectedPortTypes((?Prt,?Rest),?Specs) ==> (portType(?Prt,Specs,()),connectedPortTypes(Rest,Specs));
    #connectedPortTypes((),?Specs) ==> ();
    
    #portType(?Res,respond(?Res,?Tp),?Fl) ==> port of Tp;
    #portType(?Res,originate(?Res,?Tp),?Fl) ==> port of Tp;
    #portType(?Res,#(?L;?R)#,?Fl) ==> portType(Res,R,portType(Res,L,Fl));
    #portType(?Res,?Sp,?Fl) ==> Fl;
    
    #generateFromSchema(?Local,#(?L;?R)#) ==> generateFromSchema(#*generateFromSchema(Local,R),L);
    #generateFromSchema(?Local,#(?Ch has type ?Tp)#) ==> generateDeflt(Ch,Tp,#*pickLocal(Ch,Local));
    
    #pickLocal(?Ch,?Stmts) ==> localPick(Stmts,((),())) ## {
      #localPick(#(?L;?R)#,?So) ==> localPick(R,#*localPick(L,So));
      #localPick(Ch = ?D,(?Lc,?Ot)) ==> (glom(D,Lc),Ot);
      #localPick(?Ch2=?D,(?Lc,?Ot)) ==> (Lc,glom(Ch2=D,Ot));
      #localPick(?D,(?Lc,?Ot)) ==> (Lc,glom(D,Ot));
    }
    
    #generateDeflt(?Ch,?Tp,((),?Ot)) ==> glom(genDeflt(Ch,Tp),Ot);
    #generateDeflt(?Ch,?Tp,(?Lc,?Ot)) ==> glom(Lc,Ot);
    
    #genDeflt(?Ch,stream of ?T) ==> #(Ch(#$"X") do nothing)#;
    #genDeflt(?Ch,action#@?Ar) ==> #(Ch#@ #<#:argTemplate(#:Ar)># do nothing)#;
    #genDeflt(?Ch,#(?Ar)#=>()) ==>  #(Ch#@ #<#:argTemplate(#:Ar)># do nothing)#;
    #genDeflt(?Ch,#(?Ar=>?rs)#) ==> #(Ch#@ #<#:argTemplate(#:Ar)># is raise "no data")#;
    #genDeflt(?Ch,relation of ?T) ==> #(Ch is relation{})#;
    #genDeflt(?Ch,ref relation of ?T) ==> #(var Ch := relation{})#; 
    #genDeflt(?Ch,?T) ==> #(Ch is void)#;
    
    #show(?M) ==> _macro_log($$M,M);
    
    -- only use all mode if the schema of the responder is the same as the originator's
    #validateSchema(?Schema,?Res,?Specs./respond(?Res,{?Schema}),?Chnl,?Tp,?Conns) ==> all(Res,Chnl);
    #validateSchema(?Schema,?Res,?Specs,?Chnl,?Tp,?Conns) ==> (ruleTmpl(Res,Chnl,Tp),Conns);
    
    #ruleTmpl(?Res,?Chnl,stream of ?Tp) ==> some(Res,(#$"X"),Chnl(#$"X"));
    #ruleTmpl(?Res,?Chnl,action#@?Ar) ==> tmplSomeArgs(Res,#<#:argTemplate(#:Ar)>#,Chnl);
    #ruleTmpl(?Res,?Chnl,#(?Ar=>?R)#) ==> tmplSomeArgs(Res,#<#:argTemplate(#:Ar)>#,Chnl);
    #ruleTmpl(?Res,?Chnl,ref ?Tp) ==> some(Res,reference,Chnl);
    #ruleTmpl(?Res,?Chnl,?Tp) ==> some(Res,void,Chnl);
    
    #tmplSomeArgs(?Rs,?Args,?Ch) ==> some(Rs,Args,Ch#@Args);

    -- process notifies
    #processNotifies(?lVar,?Schema,?OP,?Specs) ==> notifyProc(lVar,notifies(Schema,Schema,OP,Specs,()));
    
    #notifies(#(?L;?R)#,?Schema,?OP,?Specs,?Rules) ==> notifies(R,Schema,OP,Specs,notifies(L,Schema,OP,Specs,Rules));
    #notifies(#(?Ch has type stream of ?Tp)#,?Schema,?OP,?Specs,?Rules) ==> 
        mergeRules(#*notifyRule(Ch,#*notifyConns(OP,Ch,Specs,Specs,Schema,())),Rules);
    #notifies(#(?Ch has type ?tV~?Tp)#,?Schema,?OP,?Specs,?Rules) ==> notifies(#(Ch has type Tp)#,Schema,OP,Specs,Rules);
    #notifies(#(?Ch has type for all ?tV such that ?Tp)#,?Schema,?OP,?Specs,?Rules) ==> notifies(#(Ch has type Tp)#,Schema,OP,Specs,Rules);
    #notifies(?Tp,?Schema,?OP,?Specs,?Rules) ==> Rules;
    
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,(?Vol)),?Specs,?Schema,?Conns) ==> notifyConns(OP,Chnl,connect(OP,Res,Vol),Specs,Schema,Conns); 
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(volunteer ?X on ?Chnl as ?X on ?ResCh)#),?Specs,?Schema,?Conns) ==> (ruleTmpl(Res,ResCh,stream of any),Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(volunteer ?X on ?Chnl as ?Y on ?ResCh)#),?Specs,?Schema,?Conns) ==> (some(Res,(X),ResCh(Y)),Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(volunteer notify #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,stream of any,Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(volunteer #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,stream of any,Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(?X on ?Chnl as ?X on ?ResCh)#),?Specs,?Schema,?Conns) ==> (ruleTmpl(Res,ResCh,stream of any),Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(?X on ?Chnl as ?Y on ?ResCh)#),?Specs,?Schema,?Conns) ==> (some(Res,(X),ResCh(Y)),Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(notify #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,stream of any,Conns);
    #notifyConns(?OP,?Chnl,connect(?OP,?Res,#(#(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,stream of any,Conns);
    #notifyConns(?OP,?Chnl,#(?L;?R)#,?Specs,?Schema,?Conns) ==> notifyConns(OP,Chnl,R,Specs,Schema,notifyConns(OP,Chnl,L,Specs,Schema,Conns));
    #notifyConns(?OP,?Chnl,?Oth,?Specs,?Schema,?Conns) ==> Conns;
    
    #notifyRule(?Strm, all(?Port,?Chnl)) ==> all((Port._notify(Fn),()));
    #notifyRule(?Strm, ?Conns) ==> ((),Strm=#(Strm(#$"X") do invokeNotify(Conns,#$"X") )#);
    
    #invokeNotify((some(?Port,(?lArg),?Chnl#@(?rArg)),()),?X) ==> Port._notify(channelProc(Chnl(rArg./lArg->X)));
    #invokeNotify((all(?Port,?Chnl),()),?X) ==> Port._notify(channelProc(Chnl(X)));
    #invokeNotify((),?X) ==> nothing;
    #invokeNotify((some(?Port,(?lArg),?Exp),?More),?X) ==> #(Port._notify(channelProc(Exp./lArg->X));invokeNotify(More,X))#;
    #invokeNotify((all(?Port,?Chnl),?More),?X) ==> #(Port._notify(channelProc(Chnl,X));invokeNotify(More,X))#;
    
    #channelProc(?Exp) ==> (procedure(#$"A") do #$"A".Exp);
    #channelProc(?Chnl,?X) ==> (procedure(#$"A")do #$"A".Chnl(X));
    
    #notifyProc(?lVar,(?Streams, ()))  ==> ( #(_notify(Fn) do Streams)#, ()); -- push notifications
    #notifyProc(?lVar, ( (), ?Local)) ==> ( #(_notify(Fn) do Fn(lVar))#, Local); -- process notifies locally
    #notifyProc(?lVar, ()) ==> ( #(_notify(Fn) do nothing)#, ()); -- drop all notifications
    
    -- process schema for requests
    #processRequests(?lVar,?Schema,?OP,?Specs) ==> requestProc(lVar,#*requests(Schema,Schema,OP,Specs,()));
    
    #requests(#(?L;?R)#,?Schema,?OP,?Specs,?Rules) ==> requests(R,Schema,OP,Specs,#*requests(L,Schema,OP,Specs,Rules));
    #requests(#(?Ch has type action#@?Tp)#,?Schema,?OP,?Specs,?Rules) ==> 
        mergeRules(#*requestRule(OP,Ch,Tp,requestConns(OP,Ch,Tp=>(),Specs,Specs,Schema,())),Rules);
    #requests(#(?Ch has type #(?Tp)#=>())#,?Schema,?OP,?Specs,?Rules) ==> 
        mergeRules(#*requestRule(OP,Ch,Tp,requestConns(OP,Ch,Tp=>(),Specs,Specs,Schema,())),Rules);
    #requests(#(?Ch has type for all ?V such that ?Tp)#,?Schema,?OP,?Specs,?Rules) ==> requests(#(Ch has type Tp)#,?Schema,?OP,?Specs,?Rules);
    #requests(?Tp,?Schema,?OP,?Specs,?Rules) ==> Rules;
    
    -- Find the applicable connections
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,(?Vol)),?Specs,?Schema,?Conns) ==> requestConns(OP,Chnl,Tp,connect(OP,Res,Vol),Specs,Schema,?Conns); 
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer request #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer #(?Chnl)##@?pArgs as ?R)#),?Specs,?Schema,?Conns) ==> (some(Res,pArgs,R),Conns);
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(#(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(request #(identifier?X)# as ?X)#),?Specs,?Schema,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #requestConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(#(?Chnl)##@?pArgs as ?R)#),?Specs,?Schema,?Conns) ==> (some(Res,pArgs,R),Conns);
    #requestConns(?OP,?Chnl,?Tp,#(?L;?R)#,?Specs,?Schema,?Conns) ==> requestConns(OP,Chnl,Tp,R,Specs,Schema,requestConns(OP,Chnl,Tp,L,Specs,Schema,Conns));
    #requestConns(?OP,?Chnl,?Tp,?Specs,?Sp,?Sch,?Conns) ==> Conns;
      
    #ruleSignal((),?Signal) ==> Signal;
    #ruleSignal(all(?Res,?Chnl),globalRequest) ==> globalRequest;
    #ruleSignal((all(?Res,?Chnl),?More),globalRequest) ==> ruleSignal(More,globalRequest);
    #ruleSignal((some(?A,?RP,?R),?More),?Signal) ==> localRequest;
    
    #localRequest(?OP,?Chnl,?Tp, ?Conds) ==> localRequestRule(Chnl,#<#:argTemplate(#:Tp)>#,localRequestRules(Chnl,Conds,(),(),()));
    
    #argTemplate(()) ==> ();
    #argTemplate((?A,?M)) ==> (#$"X",argTemplate(M));
    
    #localRequestRules(?Chnl,(),?Calls,?Global,?Rules) ==> (Calls,Global,Rules);
    #localRequestRules(?Chnl,(all(?Res),?More), ?Calls, ?Global, ?Rules) ==>
      localRequestRules(Chnl,More,Calls, glom(Res._request(Fn,Qt,Fr),Global),Rules);
    #localRequestRules(?Chnl,(some(?Res,?pArgs,?R),?More),?Calls,?Global,?Rules) ==>
      localRequestRules(Chnl,More,(#$Chnl,Calls),Global,
                                     glom(#(private #$Chnl#@pArgs do Res._request((procedure(#$"X") do #$"X".R), 
                                                      (function() is quote(R)),
                                                      (function() is freeHash(pArgs))))#,Rules));
    #localRequestRule(?Chnl,?Tmplate,(?Calls,?Globals,?Rules)) ==> (Globals, glom( Chnl=#(Chnl#@Tmplate do reqCall(Calls,Tmplate,()))#, Rules));
    
    #reqCall( (), ?T, ?S) ==> S;
    #reqCall( (?L, ?R), ?T, ?S) ==> reqCall(R, T, glom(L#@T,S));
    #reqCall( ?C, ?T, ?S) ==> glom(C#@T,S);
                                                          
    #globalRequest(?OP,?Chnl,?Tp,?Conds) ==> all((requestConds(Conds,nothing),()));
   
    #requestConds( (), ?Goal) ==> Goal;
    #requestConds( all(?Res,?Chnl), nothing ) ==> Res._request(Fn,Qt,Fr);
    #requestConds( (all(?Res,?Chnl),?More), nothing ) ==> requestConds(More,Res._request(Fn,Qt,Fr));
    #requestConds( (all(?Res,?Chnl),?More), ?Goal ) ==> Goal;
     
    #requestRule(?OP,?Chnl,?Tp,?Conns) ==> ruleSignal(Conns,globalRequest) #@ (OP, Chnl,Tp, Conns); 
        
    #requestProc(?lVar, (?Conds, ()) ) ==> ( #(_request(Fn,Qt,Fr) do Conds)#, () ); -- push requests down
    #requestProc(?lVar, (?Calls, ?Local) ) ==> ( #(_request(Fn,Qt,Fr) do { glom(Fn(lVar),Calls)} )#, Local); -- process requests locally
    #requestProc(?lVar, () ) ==> ( #(_request(Fn,Qt,Fr) do nothing)#, ());
    #requestProc(?lVar, ?Rules ) ==> ((), Rules);
    
    -- process schema for queries
    #processQueries(?lVar,?Schema,?OP,?Specs) ==> queryFun(lVar,queries(Schema,OP,Schema,Specs,()));
    
    #queries(#(?L;?R)#,?OP,?Schema,?Specs,?Rules) ==> queries(R,OP,Schema,Specs,queries(L,OP,Schema,Specs,Rules));
    #queries(#(?Ch has type action#@?Tp)#,?OP,?Schema,?Specs,?Rules) ==> Rules;
    #queries(#(?Ch has type #(?Tp)#=>())#,?OP,?Schema,?Specs,?Rules) ==> Rules;
    #queries(#(?Ch has type stream of ?Tp)#,?OP,?Schema,?Specs,?Rules) ==> Rules;
    #queries(#(?Ch has type ?V~?Tp)#,?Op,?Schema,?Specs,?Rules) ==> queries(#(Ch has type Tp)#,Op,Schema,Specs,Rules);
    #queries(#(?Ch has type for all ?V such that ?Tp)#,?Op,?Schema,?Specs,?Rules) ==> queries(#(Ch has type Tp)#,Op,Schema,Specs,Rules);
    #queries(#(?Ch has type ?Tp)#,?OP,?Schema,?Specs,?Rules) ==> mergeEquations(queryEqn(OP,Ch,Tp,queryConns(OP,Ch,Tp,Specs,Schema,Specs,())),Rules);
    
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,(?Vol)),?Schema,?Specs,?Conns) ==> queryConns(OP,Chnl,Tp,connect(OP,Res,Vol),Schema,Specs,Conns); 
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer #(identifier?X)# as ?X)#),?Schema,?Specs,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer query #(identifier?X)# as ?X)#),?Schema,?Specs,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(volunteer #(?Chnl)##@?pArgs as ?R)#),?Schema,?Specs,?Conns) ==> (some(Res,pArgs,R),Conns);
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(#(identifier?X)# as ?X)#),?Schema,?Specs,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(query #(identifier?X)# as ?X)#),?Schema,?Specs,?Conns) ==> validateSchema(Schema,Res,Specs,Chnl,Tp,Conns);
    #queryConns(?OP,?Chnl,?Tp,connect(?OP,?Res,#(#(?Chnl)##@?pArgs as ?R)#),?Schema,?Specs,?Conns) ==> (some(Res,pArgs,R),Conns);
    #queryConns(?OP,?Chnl,?Tp,#(?L;?R)#,?Schema,?Specs,?Conns) ==> queryConns(OP,Chnl,Tp,R,Schema,Specs,queryConns(OP,Chnl,Tp,L,Schema,Specs,Conns));
    #queryConns(?OP,?Chnl,?Tp,?Oth,?Schema,?Specs,?Conns) ==> Conns;
    
    #queryEqn(?OP,?Chnl,?Tp,all(?Res,?Chnl)) ==> (Res._query(Fn,Qt,Fr),());
    #queryEqn(?OP,?Chnl,?Tp,(some(?Res,reference,?R),()))==> 
       ((),Chnl=#(Chnl is Res._query((function(#$"X") is ref #$"X".R),
                                             (function() is quote(R)),
                                             (function() is map of {})))#);
    #queryEqn(?OP,?Chnl,?Tp,(some(?Res,void,?R),()))==> 
       ((),Chnl=#(Chnl is Res._query((function(#$"X") is #$"X".R),
                                             (function() is quote(R)),
                                             (function() is map of {})))#);
    #queryEqn(?OP,?Chnl,?Tp,(some(?Res,?Args,?R),())) ==> 
       ((),Chnl=#(Chnl#@Args is Res._query((function(#$"X") is #$"X".R), 
                                     (function() is quote(R)),
                                     (function() is freeHash(R))))#);
    #queryEqn(?OP,?Chnl,?Tp,(some(?Res,?Args,?R),?More)) ==> 
	error("query channel $Chnl can be responded to by multiple ports",((),()));
    #queryEqn(?OP,?Chnl,?Tp,()) ==> (#(raise "cannot respond to query $(display_quoted(Qt()))")#,());
    
    #extractPorts((),?Res)==>Res;
    #extractPorts((some(?Res,?Args,?R),?More),?SoFar) ==> extractPorts(More,SoFar and Res);
    #extractPorts((all(?Res),?More),?SoFar) ==> extractPorts(More,SoFar and Res);
    #extractPorts(all(?Res),?SoFar) ==> SoFar and Res;
    
    #mergeEquations( (?Cond, ?Inner), (?C2,?I2)) ==> (pickEqn(Cond,C2), glom(Inner,I2)) ## {
      #pickEqn((),?R) ==> R;
      #pickEqn(?L,?R) ==> L; -- we will ignore because they should be duplicate
    };
    #mergeEquations( (), ?R) ==> R;
    #mergeEquations( ?L, ()) ==> L;
    
    #freeHash(()) ==> map of {};
    #freeHash(?F#@?Args) ==> map of { unPack(#:Args) } ## {
      #unPack((?L,())) ==> unPack(L);
      #unPack((?L,?R)) ==> #( unPack(L);unPack(R))#;
      #unPack(?A) ==> #($$A -> (A cast any))#;
    };
        
    #queryFun(?lVar, (?qExp,()) ) ==> (#(_query(Fn,Qt,Fr) is qExp)#, ());
    #queryFun(?lVar, ((), ?Local) ) ==> (#(_query(Fn,Qt,Fr) is Fn(lVar))#, Local);
    #queryFun(?lVar, ()) ==> (#(_query(Fn,Qt,Fr) is raise "cannot respond to query $(display_quoted(Qt()))")#, ());
        
    #glom(?A,()) ==> A;
    #glom((),?A) ==> A;
    #glom(?A,?B) ==> #(A;B)#;
    
    #mergeRules( all((?Cond,?Inner)), ?XX) ==> (Cond,Inner);
    #mergeRules( ?XX, all((?Cond,?Inner))) ==> (Cond,Inner);
    #mergeRules( (?Cond, ?Inner), (?C2,?I2)) ==> (glom(Cond,C2), glom(Inner,I2));
    #mergeRules( (), ?R) ==> R;
    #mergeRules( ?L, ()) ==> L;
  };
}