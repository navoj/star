quoting is package{
  prc main() do
  {
    def P is quote("Alpha");
    def Q is quote((query A with all X where (X,unquote(P)) in M));
    
    logMsg(info,"$Q");
    
    assert quote("alpha")=quote("alpha");
    assert quote(A)=quote(A);
    assert quote(f(A))=quote(f(A));
    
    assert Q=quote((query A with all X where (X,unquote(P)) in M));
    
    def QS is <| "An interpolated $String" |>;
    logMsg(info,"QS=$QS");
    
    assert Q=quote((query A with all X where (X,"Alpha") in M));
    
    logMsg(info,"$(quote(#(def X is found())#))");
  }
}