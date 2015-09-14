substTest is package{
  import person;
  
  fun substAgeFun(X,V) is X substitute{ age := some(V) };
  
  prc main() do {
    var J := someone{
      name = "Joe";
    }
    
    assert J.age=none;
    
    def K is J substitute { age := some(10.0)};
    
    assert K.age has value 10.0;
    assert J.age=none;
    
    def L is substAgeFun(J,10.0);
    assert L = K;
  }
}