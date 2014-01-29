recordThis is package{

  RR is let{
    This is memo{
      F=F; G=G;
    }
    
    F(X) is let{
      T is This();
    } in G(X);
    
    G(X) is X;
  } in This();
  
  main() do {
    assert RR.F(3)=3;
  }
}