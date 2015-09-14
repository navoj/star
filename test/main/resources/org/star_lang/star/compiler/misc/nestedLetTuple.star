nestedLetTuple is package{
  fun foo() is let{
    def (a, (b, c)) is (1, (2, 3));
  } in (a,b,c);
  
  fun bar(X) is let{
    def (a, (b, c)) is X;
  } in (a,b,c);
  
  def foo2 is let {
    def ((a,b), c) is ((1,2),3);
  } in 0;
  
  prc main() do {
    assert foo()=(1,2,3);
    
    assert bar((1,(2,3))) = (1,2,3);
    
    assert foo2=0;
  }
}
    
  