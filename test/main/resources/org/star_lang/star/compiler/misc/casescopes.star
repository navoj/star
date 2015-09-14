casescopes is package{
  fun test(X) is valof{
    def a is 1;
    
    switch X in {
      case a do valis "yep";
      case b default do valis "no"
    }
  };
  
  prc main() do {
    assert test(1)="yep";
    assert test(2)="no";
  }
}