freePtn is package{
  -- test references to free variables in pattern abstractions
  
  fun find(X,cons(U1,cons(U2,cons(U3,cons(Y,nil))))) is search(((XX) from XX where XX=Y),X);
  
  fun search(Ptn,cons of [H,..T]) where H matches Ptn(XX) is XX
   |  search(Ptn,cons of [_,..T]) is search(Ptn,T);
  
  prc main() do {
    def XX is find(cons of ["one", "two", "three"],cons of ["alpha","beta","gamma","two"]);
    logMsg(info,"XX=$XX");
    assert XX="two";
  }
}