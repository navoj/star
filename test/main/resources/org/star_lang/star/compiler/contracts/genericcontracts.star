genericcontracts is package{
  import random;
  
  prc main() do {
    def G is StdGen(0,1000);
    
    def (R1,G1) is next(G);
    
    logMsg(info,"first random is $R1");
    
    def (R2,G2) is next(G1);
    
    logMsg(info,"second is $R2");
    
    -- only pseudo random
    assert R1=2106791562;
    assert R2=2018320191;
  }
  
}