loopingTask is package{
  import task;
  
  type found of %t is found(%t) or notFound; 
  
  fun ww(X) is task{
    var C := 0;
    for E in X do
      C := C+E;
    valis C;
  };
  
  fun ff(K,L) is task{
    for (KK,V) in L do{
      if K=KK then
        valis found(V);
    };
    valis notFound;
  };
  
  prc main() do {
    def ZZ is valof ww(list of [1,2,3,4,5]);
    assert ZZ=15;
    
    def MM is list of [(1,"alpha"), (2,"beta"), (3,"gamma"), (4,"delta")];
    def T1 is ff(3,MM);
    logMsg(info,"not yet started: $T1");
    def V1 is valof T1;
    logMsg(info,"value of search is $V1");
    assert V1=found("gamma");
    assert valof ff(5,MM)=notFound;
  }
}
