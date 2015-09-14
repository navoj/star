nodecontract is package{
  contract foo over %t is {
    bar has type (%t)=>integer;
  }
  
  implementation foo over integer is {
    fun bar(X) is X*2;
  }
  
  type tree is node{
    tt has kind type;
    XX has type tt where foo over tt;
    
    B has type (tt)=>integer;
  };
  
  prc main() do {
    def N is node{
      type tt is alias of integer;
      def XX is 23;
      fun B(X) is bar(X);
    };
    
    def B is N.B(N.XX);
    logMsg(info,"B=$B");
    assert B=46;
  }
}