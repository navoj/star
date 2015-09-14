B is package {
  fun executedB() is execB;
  
  var execB := false;
  prc funcB(p) do { 
    execB := true;
    p.funcA();
  }
}