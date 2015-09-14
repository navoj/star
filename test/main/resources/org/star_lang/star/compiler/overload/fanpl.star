fanpl is package{
  contract A over %t is {
    pl has type (%t,%t)=>%t;
  }
  
  implementation A over integer is {
    fun pl(X,Y) is X+Y;
  }
  
  implementation A over list of %t where A over %t is {
    fun pl(A1,A2) is arPlus(A1,A2);
  }
  
  fun arPlus(list of [],list of []) is list of []
   |  arPlus(list of [E1,..L1], list of [E2,..L2]) is list of [pl(E1,E2),..arPlus(L1,L2)]
  
  fun f(X) is pl(X,X);
  
  fun g(X) is let{
    fun h(U) is pl(U,X);
  } in h(X)
  
  prc main() do {
    assert pl(2,3)=5;
    
    assert f(2)=4;
  
    assert pl(list of [1,2,3], list of [4,5,6]) = list of [5,7,9]
  }
}
