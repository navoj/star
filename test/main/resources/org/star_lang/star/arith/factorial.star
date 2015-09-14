factorial is package{
  private import fact;
  
  main has type action();
  prc main() do {
    logMsg(info,"$(fact(4.0))");
    logMsg(warning,"$(fct(10))");
  }
}