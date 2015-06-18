trialLazy is package{
  private import lazySupport;
  
  -- experiment in implementing a lazy evaluation keyword
  #prefix("lazy",1300);
  
  #lazy ?Lhs is ?Exp :: statement :- Lhs :: identifier :& Exp::expression;
  
  #let{?B./#(lazy ?_ is ?_)#} in ?Bound ==> lazyTrial ## {
	fun collectLazyVars(<| lazy ?nameAst(_,Nm) is ?Exp |>, (Theta,LazyVars)) is (Theta,[(Nm,Exp),..LazyVars])
     |  collectLazyVars(<|?L;?R|>,Coll) is collectLazyVars(R,collectLazyVars(L,Coll))
     |  collectLazyVars(Stmt,(Theta,Rules)) is (list of [Theta..,Stmt],Rules)
    
    fun formSub(list of [],M) is M
     |  formSub(list of [(N matching nameAst(Lc,Nm),Exp),..Vars], (M,V)) is valof{ 
          def LVn is nameAst(Lc,gensym(Nm));
          valis formSub(Vars, (_set_indexed(M,N,applyAst(Lc,LVn,list of [])),list of [ <| def ?LVn is memo ?Exp |>,..V]))
        }
 
 	fun semi(X,<|{}|>) is X
 	 |  semi(X,Y) is <| ?X ; ?Y |>
 	
 	#def lazyTrial is valof{
 	  def (Others,LVars) is collectLazyVars(B, (list of [],dictionary of []))
 	  def subFun is (V,Th) => semi(macroSubstitute(V,dictionary of [],LVars),Th)
 	  
 	  def trVars is rightFold(((_,v),st)=>subFun(v,st),<|{}|>, LVars);
 	  def nBody is subFun(B,trVars);
 	  
 	  def nBound is macroSubstitute(Bound,dictionary of [],LVars);
 	  valis <| let{ ?nBody } in ?nBound |>
 	}
  }
}