lambda is package{
  -- test a potential lambda notation
  
  #right(">\\",999);
  
  # ?T >\ ?E :: expression :- T:*id :& E::expression;
  
  # identifier ? T >\ ?E ==> (function(T) is E);
  # tuple?T >\ ?E ==> (function#@T is E);
  
  main() do {
    iden is X>\X;
    
    PP is X >\ Y >\ X+Y;
    
    I is PP(1);
    K is I(2);
    
    logMsg(info,"K=$K");
    
    var QQ is (X,Y) >\ X+Y;
    logMsg(info,"Q=$(QQ(1,2))");
    assert QQ(1,2)=3
  }
} 
  