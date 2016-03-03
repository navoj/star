tasking is package{
  import compute;
  
  #task{?A} :: expression :- A ;* action;
  #task { ?B } ==> task computation {?B};
  
   -- this is a temporary test
  type task of %t is xx(%t) or drop(string);
  
  implementation (computation) over task is {
    _encapsulate(x) is xx(x);
    _combine(m, f) is switch m in {
      case drop(S) is drop(S);
      case xx(v) is f(v);
    };
    _abort(S) is drop(S);
    
    _handle(drop(M),EF) is EF(M);
    _handle(O,_) is O;
  }
  
  implementation execution over task is {
    _perform(xx(X)) is X;
  }
}