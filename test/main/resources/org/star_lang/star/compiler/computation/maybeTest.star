maybeTest is package{
  import maybe;
  
  fun ff(K,L) is maybe computation{
    for (KK,V) in L do{
      if K=KK then
        valis V;
    };
    raise "not found";
  };
  
  fun id(X) is X;
  
  prc main() do {
    def MM is list of [(1,"alpha"), (2,"beta"), (3,"gamma"), (4,"delta")];
    
    logMsg(info,"value of ff(2,MM) is $(valof ff(2,MM))");
    
    assert valof ff(2,MM) = "beta"
    
    def R2 is valof ff(5,MM) on abort ((exception(_,E,_)) => E cast string);
    
    logMsg(info,"value of ff(5,MM) is $(R2)");

    assert R2 = "not found";
  }
} 