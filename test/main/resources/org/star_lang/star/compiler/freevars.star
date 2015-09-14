freevars is package{

  outer has type (integer,integer) =>integer;
  fun outer(A,B) is let{
  
    inner has type (integer) =>integer;
    fun inner(X) is A+X;
    
    further has type (integer) =>integer;
    fun further(X) is let{
      inn has type (integer) =>integer;
      fun inn(U) is inner(U*B);
    } in inn(X);
  } in further(A);
  
  main has type action();
  prc main() do {
    logMsg(warning, "$(outer(10,4))")
  };

}