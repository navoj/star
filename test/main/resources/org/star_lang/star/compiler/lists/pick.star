pick is package{
  -- sample programs to check out the match compiler with

  pick has type (list of %t,integer) => %t;
  fun pick(list of [X,.._],0) is X
   |  pick(list of [_,X,.._],1) is X
   |  pick(list of [_,_,X,.._],2) is X
   |  pick(list of [_,_,_,..R],N) is pick(R,N-3)
  
  main has type action();
  prc main() do {
    def L is list of [1,2,3,4,5,6];
    logMsg(info, "pick 3rd from $L = $(pick(L,2))");
    logMsg(info, "second element is $(L[1])");
    
    assert pick(L,4)=5;
    assert pick(L,2)=3;
  };
  
} 