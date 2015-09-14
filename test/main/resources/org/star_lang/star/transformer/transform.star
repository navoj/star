transform is package{
  import support;
  
  #infix("bound to",900);
  #infix("if",1200);
  #prefix("depends on",1100);
  #prefix("produces",1100);
  
  #?X bound to ?Y ==> Y matches X;
  
  # transformer { ?Rules } :: expression :- Rules ;* transformerRule;
  
  # transformer { ?Rules } ==> actor{ generateTransformer(Rules) };
  
  # depends on ?R :: transformerRule :- R :: idList;
  # produces ?R :: transformerRule :- R :: idList;
  # ?Head if ?Body :: transformerRule :- Head :: predication :& Body :: condition;
  
  # ?L in ?R :: predication :- L::expression :& R::id;  
  
  # ?L 'n ?R :: idList :- L::idList :& R::idList;
  # identifier :: idList;
  
  # generateTransformer(?Rules) ==> buildTransform(ruleFuns) ## {
    # incoming ==> findIncoming(Rules);
    # outgoing ==> findOutgoing(Rules);
    # affects ==> processTheta(Rules,findAffected,());
    # dependencies ==> processTheta(Rules,findDependency,());
    # argTemplates ==> processList(dependencies,genTemplate,iden,());
    # ruleFuns ==> processTheta(Rules,genRuleFun,{});
    
    # incomingDecls ==> genStmts(#*incoming,makeDecl) ## {
      # makeDecl(?Id) ==> #( var Id#+_spt := support of [] )#;
    };
    
    # findIncoming(?Rls) ==> processTheta(Rls,findIn,()) ## {
      # findIn(#(depends on ?I)#,?SoFar) ==> processSN(I,getId,SoFar);
      # findIn(?X,?SoFar) ==> SoFar;
    }
    
    # findOutgoing(?Rls) ==> processTheta(Rls,findOut,()) ## {
      # findOut(#(produces ?O)#,?SoFar) ==> processSN(O,getId,SoFar);
      # findOut(?X,?SoFar) ==> SoFar;
    }
    
    # getId(identifier?I, ?SoFar) ==> (I,SoFar);
    # getId(?X,?SoFar) ==> ignore(error("expecting identifier, not $$X"),SoFar);
    
    # findAffected(#(?Exp in ?Nm if ?Cond)#,?Deps) ==> processCond(Cond,AddAffected,Deps) ## {
      # AddAffected(?Id,?Dps) ==> addDepen(Dps) ## {
        # addDepen(((Id, ?D),?R)) ==> ((Id,(Nm,D)),R);
        # addDepen((?X,?R)) ==> (X,addDepen(R));
        # addDepen(()) ==> ((Id,(Nm,())),());
      }
    };
    # findAffected(?Rl,?Deps) ==> Deps;
    
    # findDependency(#(?Exp in ?Nm if ?Cond)#,?Deps) ==> (Nm,processCond(Cond,AddToDep,())) ## {
      # AddToDep(?Id,?Dps) ==> (Id,Dps)
    };
    # findDependency(?Rl,?Deps) ==> Deps;
    
    # genTemplate((?Nm,?Deps),?SoFar) ==> ((Nm,gen_#+Nm#@#<Deps>#),SoFar);
    
    # genRuleFun(#(?Exp in ?Nm if ?Cond)#,?SoFar) ==> glom(#(fun findTemplate(Nm) is support of { all Exp where Cond })#,#*SoFar);
    # genRuleFun(?Rl,?SoFar) ==> SoFar;
    
    # glom(?A,{}) ==> A;
    # glom(?A,?B) ==> #(B;A)#;
    
    # findTemplate(?Nm) ==> locate(Nm,argTemplates,Nm());
  };
  
  # processTheta(#(?L;?R)#,?App,?SoFar) ==> processTheta(R,App,processTheta(L,App,SoFar));
  # processTheta(#(?L;)#,?App,?SoFar) ==> App(L,SoFar);
  # processTheta(?S,?App,?SoFar) ==> App(S,SoFar);
  
  # processSN(?L 'n ?R, ?App, ?SoFar) ==> processSN(#*R,App,processSN(#*L,App,SoFar));
  # processSN(?X,?App,?SoFar) ==> App(X,SoFar);
  
  # processList(?List,?App,?Deflt,?Init) ==> procList(List,Init) ## { 
    # procList((),?SoFar) ==> Deflt(SoFar);
    # procList((?L,?R),?SoFar) ==> procList(R,procList(L,SoFar));
    # procList(?X,?SoFar) ==> App(X,SoFar);
  }
  
  # processCond(?Ptn in ?Nm,?App,?SoFar) ==> App(Nm,SoFar);
  # processCond(?L and ?R,?App,?SoFar) ==> processCond(R,App,processCond(L,App,SoFar));
  # processCond(?L or ?R, ?App, ?SoFar) ==> processCond(R,App,processCond(L,App,SoFar));
  # processCond(not ?C,?App,?SoFar) ==> processCond(C,App,SoFar);
  # processCond((?C),?App,?SoFar) ==> processCond(C,App,SoFar);
  # processCond(?E matches ?P,?App,?SoFar) ==> processExxp(E,App,SoFar);
  # processCond(#(?P bound to ?E)#,?App,?SoFar) ==> processExp(E,App,SoFar);
  # processCond(?C,?App,?SoFar) ==> SoFar;
  
  # processExp(?E ./ ?Q where ?C, ?App, ?SoFar) ==> processExp(E./void,App,processCond(C,App,SoFar));
  # processExp(?E, ?App, ?SoFar) ==> SoFar;
  
  # genStmts(?List,?App) ==> processList(#*List,genSemi,iden,{}) ## {
    # genSemi(?It,{}) ==> App(It);
    # genSemi(?It, ?Stmts) ==> #(Stmts;App(It))#;
    # iden(?In) ==> In;
  };
  
  # locate(?Nm,((?Nm,?Dp),?R),?Def) ==> Dp;
  # locate(?Nm,(?L,?R),?Def) ==> locate(Nm,R,Def);
  # locate(?Nm,(),?Def) ==> Def;
  
  # ignore(?L,?R) ==> right(#*L,R) ## {
     #right(?LL,?RR) ==> R;
    }
}