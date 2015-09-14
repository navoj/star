firstTest is package{
  second has type ((%s,%t,%u)) => %t;
  fun second((_,X,_)) is X;
  
  main has type action();
  prc main() do {
    logMsg(info,"$(second((1,2,3)))");
  }
}
