dependtype is package{
  type foo of %t where bar over %t is nofuss(%t);
  
  contract bar over %t is {
    bore has type (%t)=>string;
  }
  
  implementation bar over string is {
    fun bore(X) is X;
  }
  
  alpha has type (foo of %t) => string;
  fun alpha(nofuss(X)) is bore(X);
  
  prc main() do
  {
    assert alpha(nofuss("fred"))="fred";
  }
}