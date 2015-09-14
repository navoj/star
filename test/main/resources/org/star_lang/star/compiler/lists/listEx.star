listEx is package{
  -- test out example cons expressions
  
  prc main() do {
    def L0 is cons of [];
    
    def L1 is cons of [1];
    
    def L2 is cons of [1,2,3,4];
    
    def L3 is cons of [-1,-2,..L2]
    
    def L4 is cons of [L2..,5,6,7]
    
    def L5 is cons of [[-2,-1,..L2]..,5,6,7]
    
    logMsg(info,"L5=$L5");
    assert L5=cons of [-2,-1,1,2,3,4,5,6,7];
  }
}