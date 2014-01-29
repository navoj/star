tupleAssign is package{

  main() do {
    var T := 1;
    var A := "one"
    
    (T,A) := (2,"beta");
    
    assert T=2 and A="beta"
  }
}
   