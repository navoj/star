unravel is package{

  unravel has type (%coll)=>(%left,%right) where
    sequence over %coll determines ((%l,%r)) and
    sequence over %left determines %l and
    sequence over %right determines %r;

  fun unravel(LL) is let{
    fun unravl([],L,R) is (L,R)
     |  unravl([(El,Er),..M],L,R) is unravl(M,[L..,El],[R..,Er])
  } in unravl(LL,[],[]);
    
  prc main() do {
    def Lin is list of [(1,"alpha"), (2,"beta"), (3,"gamma")];
    
    def (Lf,Rg) is unravel(Lin);
    
    assert Lf = list of [1,2,3];
    assert Rg = list of ["alpha","beta","gamma"];
  }
} 