lambda is package{
  -- test a potential lambda notation
  
  #right(">\\",999);
  
  # ?T >\ ?E :: expression :- T:*id :& E::expression;
  
  # identifier ? T >\ ?E ==> (T) => E;
  # tuple?T >\ ?E ==> T => E;
  
  prc main() do {
    def iden is X>\X;
    
    def PP is X >\ Y >\ X+Y;
    
    def I is PP(1);
    def K is I(2);
    
    logMsg(info,"K=$K");
    
    def QQ is (X,Y) >\ X+Y;
    logMsg(info,"Q=$(QQ(1,2))");
    assert QQ(1,2)=3
  }
} 
  