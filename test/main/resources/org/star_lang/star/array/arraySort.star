arraySort is package{
  prc main() do {
    def S is list of [("alpha",1), ("beta",2), ("alpha",0), ("beta",10), ("gamma",1)]
    
    logMsg(info,"Sort $S to\n$(sort(S,<))");
    
    assert sort(S,<) = list of [("alpha",0), ("alpha",1), ("beta",2), ("beta",10), ("gamma",1)];
    
    PS has type list of integer;
    def PS is iota(1,300,1);
    
    logMsg(info,"positive cons = $PS");
    
    CS has type list of integer;
    def CS is iota(300,1,-1);
    logMsg(info,"negative cons = $CS");
    
    
    def SS is sort(CS,<);
    
    logMsg(info,"sorted is $SS");
    
    assert SS = PS;
  }
}