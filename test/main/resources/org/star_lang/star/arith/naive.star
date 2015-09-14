naive is package{
  fun append(nil,X) is X
   |  append(cons(H,T),X) is cons(H,append(T,X))
  
  fun reverse(nil) is nil
   |  reverse(cons(H,T)) is append(reverse(T),cons(H,nil));
  
  fun iot(K,K) is cons(K,nil)
   |  iot(M,X) is cons(M,iot(M+1,X));
  
  fun bench(Count,Run) is valof{
    def LL is iot(1,Count);
    def St is nanos();
    for Ix in iota(1,Run,1) do{
      def RR is reverse(LL);
    }
    def Tm is (nanos()-St)/Run as long;
    def lips is 500000000L*((Count+Count*Count)as long)/Tm;
    valis lips;
  }
  
  prc main() do {
    for C in range(30,300,10) do
      logMsg(info,"bench of $C is $(bench(C,1000))");
  }
}