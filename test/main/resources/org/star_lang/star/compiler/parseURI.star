parseURI is package{
  prc main() do {
    def u1 is "file:/alpha/beta/gamma?question=answer\#fragment" as uri;
    
    logMsg(info,"u1=$u1");
    
    def u2 is "std:star.star" as uri;
    
    logMsg(info,"u2=$u2");
  }
}