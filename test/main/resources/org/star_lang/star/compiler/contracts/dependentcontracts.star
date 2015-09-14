dependentcontracts is package{
  
  contract foo over %e determines %t is {
    bar has type (%e)=>%t;
  }
  
  type hollow of %t is hollow(%t);
  
  type solid is solid(string)
  
  implementation foo over hollow of %e determines %e is {
    fun bar(hollow(X)) is X;
  }
  
  implementation foo over solid determines string is {
    fun bar(solid(X)) is X;
  }
  
  prc main() do
  {
    assert "fred" = bar(solid("fred"));
    
    assert 23 = bar(hollow(23));
  }
}