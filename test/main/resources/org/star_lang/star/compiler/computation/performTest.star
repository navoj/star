performTest is package{
  type maybe of %t is possible(%t) or impossible(exception);
  
  implementation (computation) over maybe is {
    fun _encapsulate(X) is possible(X)
    
    fun _combine(possible(X),F) is F(X)
     |  _combine(impossible(E),_) is impossible(E)
    
    fun _abort(R) is impossible(R)
    
    fun _handle(impossible(R),EF) is EF(R)
     |  _handle(M,EF) is M;
  }
  
  implementation execution over maybe is {    
    fun _perform(possible(X),_) is X
     |  _perform(impossible(R),F) is F(R)
  }
  
  fun ff(LL) is maybe computation{
    for L in LL do{
      perform pp(L);
    }
    valis ();
  } 
  
  fun pp(L) is maybe computation{
    for (KK,V) in L do
      logMsg(info,"KK=$KK,V=$V");
      
    valis ()
  };
  
  fun doIf(LL,K) is maybe computation{
    if present LL[K] then
      valis ();
      
    raise "not found"
  }
  
  fun handle(LL,K) is maybe computation{
    try { 
      for (KK,V) in LL do {
        if V>K then
          raise "over #K"
      }
    } on abort { case exception(_,XX,_) do logMsg(info,"abort message: #(XX cast string)") };
    valis ()
  }

  fun id(X) is X;
  
  prc main() do {
    def L1 is list of [(1,"alpha"), (2,"beta"), (3,"gamma"), (4,"delta")];
    def L2 is list of [(5,"eta")];
    def MM is list of [L1, L2];
    
    perform ff(MM);
    
    perform doIf(L1,1);
    
    perform doIf(L1,1) on abort { case X do logMsg(info,"Got exception (1): $X"); }
    
    perform doIf(L1,5) on abort { case X do logMsg(info,"Got exception (2): $X"); }
    
    def HH is handle(L1,"omega");
    
    perform HH;
    
    perform handle(L1,"beta");
  }
}