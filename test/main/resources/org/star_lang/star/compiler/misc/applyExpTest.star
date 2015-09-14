applyExpTest is package{
 
  add has type (integer,integer)=>integer;
  fun add(X,Y) is X+Y;
  
  fun pr(X) is (X,X);
  
  prc main() do {
    def X is 10;
    
    logMsg(info,"X=$X");
    
    logMsg(info,"pr(X)=$(pr(X))");
    
    logMsg(info,"add(pr(X)) = $(add@pr(X))");
    
    assert add@pr(X)=20;
    
    assert add@pr(add@pr(10))=40;
  }
}