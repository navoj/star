functions is package{

  dbl has type ((%t) =>%t) => ((%t) =>%t);
  fun dbl(F) is let{
    -- ff has type (%t) =>%t;
    fun ff(X) is F(F(X));
  } in ff;
  
  sum has type (integer) =>integer;
  fun sum(x) is x+x;
  
  prc main() do {
    logMsg(info,"doubling sum: $(dbl(sum)(3))");
  }
}