overloadencap is package{
  -- test some overloading resolution in the context of encapsulated types
  
  type combo of %t is combo{
    t1 has kind type;
    t2 has kind type;
    ast1 has type (%t)=>t1;
    ast2 has type (%t)=>t2;
    cmp has type (t1,t2)=>boolean;
  };
  
  less has type (integer,integer)=>boolean;
  fun less(X,Y) is X<Y;
  
  prc main() do{
    def C is combo{
      type integer counts as t1;
      type integer counts as t2;
      fun ast1(X) is X;
      fun ast2(X) is X;
      cmp has type (integer,integer)=>boolean;
      def cmp is (<);
    }
    
    assert C.cmp(C.ast1(1),C.ast2(2));
  }
} 