basicNamedImport is package{
  N is import NP;
  S is import SP;
  
  prc main() do {
    X has type N.tp;
    def X is N.X;
    
    logMsg(info,"X=$(__display(X))");
    
    Y has type S.tp;
    def Y is S.Y;
    
    logMsg(info,"Y=$(__display(Y))");
  }
} 
   