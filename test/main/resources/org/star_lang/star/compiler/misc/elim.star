elim is package{
    
  elim has type (%t)=>%t where equality over %e and sequence over %t determines %e;

  fun elim([]) is []
   |  elim([H,H,..T]) is elim([H,..T])
   |  elim([H,..T]) default is [H,..elim(T)]
    
  prc main() do {
    def L1 is list of [1,2,9,5,2,2];
    def L2 is list of [3,3,9,9,0,1,1];
       
    logMsg(info,"elim(L1) is $(elim(L1) has type list of integer)");
    logMsg(info,"elim(L2) is $(elim(L2) has type list of integer)");
       
    assert elim(L1)=list of [1,2,9,5,2];
    assert elim(L2)=list of[3,9,0,1];
   }
 }