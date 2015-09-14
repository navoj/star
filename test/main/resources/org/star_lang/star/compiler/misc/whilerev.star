whilerev is package{
  fun reverse(L) is valof{
    var r := nil;
    var l := L;
    while l matches cons(H,T) do{
      r := cons(H,r);
      l := T;
    };
    valis r;
  };
  
  fun conc(L,R) is valof{
    var l := reverse(L);
    var r := R;
    while l matches cons(H,T) do{
      r := cons(H,r);
      l := T;
    };
    valis r;
  }

  prc main() do {
    def L is cons of [1,2,3,4,5];
    def R is cons of [6,7,8]
    assert reverse(L)=cons of [5,4,3,2,1]
    
    assert conc(L,R) = cons of [1,2,3,4,5,6,7,8]
  }
}