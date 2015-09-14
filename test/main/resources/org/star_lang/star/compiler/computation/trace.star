trace is package{
  import compute;
  
  #trace{ ?A } :: expression :- A ;* action;
  #trace { ?B } ==> trace computation { ?B };
  
   -- this is a temporary test
  type trace of %t is xx(%t) or drop(exception) or zz(()=>%t);
  
  implementation (computation) over trace is {
    fun _encapsulate(x) is valof{ logMsg(info,"encap $x"); valis xx(x)};
    fun _combine(m, f) is switch m in {
      case drop(S) is drop(S);
      case xx(v) is f(v);
    };
    fun _abort(S) is drop(S);
    
    fun _handle(drop(M),EF) is valof{logMsg(info,"drop: $M"); valis EF(M)}
     |  _handle(R,_) is R;
    
  }
  
  implementation execution over trace is {
    fun _perform(xx(X),_) is X
     |  _perform(drop(MSG),EF) is EF(MSG)
  }
}