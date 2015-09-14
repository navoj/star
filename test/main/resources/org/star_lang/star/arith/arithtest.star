arithtest is package{
  square has type for all t where arithmetic over t and comparable over t such that (t) => t
  fun square(X) is times(X,X)
  
  fun times(X,Y) where X>=Y is X*Y
  
  main has type action()
  prc main() do {
    logMsg(info,"square of 4 is $(square(4))");
    logMsg(info,"times of 4 is $(times(4,4))");
    
    assert square(4)=16;
    assert times(5,4) = 20;
    
    logMsg(info,"sqrt(9)=$(sqrt(9.0))");
    assert sqrt(9.0)=3 as float;
    
    logMsg(info,"funky","9.0**0.5=$(9.0**0.5)");
    
    logMsg(info,"important","you need a tune-up");
    
    assert 9.0**0.5=3.0;
  }
}
