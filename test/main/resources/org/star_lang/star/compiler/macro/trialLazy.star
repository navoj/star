trialLazy is package{
  private import sequences;
  private import lazySupport;
  
  -- experiment in implementing a lazy evaluation keyword
  #prefix("lazy",1300);
  
  #lazy ?Lhs is ?Exp :: statement :- Lhs :: identifier :& Exp::expression;
  
  #let{?B./#(lazy ?_ is ?_)#} in ?Bound ==> lazyTrial ## {
	collectLazyVars(<| lazy ?Lhs is ?Exp |>, (Theta,LazyVars)) is (Theta,list of {(Lhs,Exp);..LazyVars});
    collectLazyVars(<|?L;?R|>,Coll) is collectLazyVars(R,collectLazyVars(L,Coll));
    collectLazyVars(Stmt,(Theta,Rules)) is (list of {Theta..;Stmt},Rules);
    
    formSub(list of {},M) is M;
    formSub(list of {(nameAst(Lc,Nm),Exp);..Vars}, (M,V)) is valof{ 
      LVn is nameAst(Lc,gensym(Nm));
      valis formSub(Vars, (_set_indexed(M,Nm,applyAst(Lc,LVn,list of {})),list of { <| var ?LVn is memo ?Exp |>;..V}))
    }
 
 	semi(X,<|{}|>) is X;
 	semi(X,Y) is <| ?X ; ?Y |>;
 	
 	#lazyTrial is valof{
 	  (Others,LVars) is collectLazyVars(B, (list of [],dictionary of {}));
 	  def subFun is (V,Th) => semi(macroSubstitute(V,dictionary of {},LVars),Th);
 	  
 	  def trVars is rightFold(subFun,<|{}|>, LVars);
 	  def nBody is subFun(B,trVars);
 	  
 	  def nBound is sub(Bound,dictionary of {},LVars);
 	  valis <| let{ ?nBody } in ?nBound |>
 	}
  }
}