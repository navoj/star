applicTest is package{
  -- test the apply notation
  
  fun foo(X,Y,Z) is X+Y*Z;
  
  prc main() do {
    def Args is (1,2,3);
    
    def R is foo(1,2,3);
    
    assert R=7;
    
    def S is foo@Args;
    assert S=7;
  }
}