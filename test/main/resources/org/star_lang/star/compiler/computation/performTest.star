performTest is package{
  import maybe
  
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

    try{
      perform doIf(L1,1)
    } on abort { case X do logMsg(info,"Got exception (1): $X"); }

    try{
      perform doIf(L1,5)
    } on abort { case X do logMsg(info,"Got exception (2): $X"); }
    
    def HH is handle(L1,"omega");
    
    perform HH;
    
    perform handle(L1,"beta");
  }
}