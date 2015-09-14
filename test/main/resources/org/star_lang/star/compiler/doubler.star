doubler is package{
  -- This tests higher functions doubling themselves

  dbl has type ((%t) =>%t) => ((%t) =>%t);
  
  fun dbl(F) is let{
    -- ff has type (%t) =>%t;
    fun ff(X) is F(F(X));
  } in ff;
  
  compose has type ((%t)=>%u,(%s)=>%t)=>((%s)=>%u);
  fun compose(F,G) is (X) => F(G(X));

  add has type (integer) => ((integer) =>integer);
  fun add(I) is let{
    a has type (integer) =>integer;
    fun a(X) is X+I;
  } in a;
  
  mul has type (integer) => ((integer)=>integer);
  fun mul(I) is let{
    m has type (integer) =>integer;
    fun m(X) is X*I;
  } in m;
  
  pls has type (integer) => ((integer) =>integer);
  fun pls(I) is (X) => X+I;
  
  tms has type (integer) => ((integer)=>integer);
  fun tms(I) is (X) => X*I;

  main has type action();
  prc main() do {
    -- first a simple test of add
    logMsg(info, "add(3)(4) is $(add(3)(4))");
    -- now we test double
    logMsg(info, "dbl(add(3))(4) is $(dbl(add(3))(4))");
    
    logMsg(info,"compose((mul(2),add(3))(5) = $(compose(mul(2),add(3))(5))");
  };
}