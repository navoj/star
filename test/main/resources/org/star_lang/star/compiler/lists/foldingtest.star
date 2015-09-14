foldingtest is package{
  -- test the folding stuff
  
  prc main() do {
    def L is cons of [1, 2, 3, 4];
    
    def T is dictionary of [1 -> "one", 2->"two", 3->"three"];
    
    assert rightFold((+),0,L) = leftFold((+),0,L)
    
    logMsg(info,"fold T= $(leftFold((A,(K,V))=>(A++V),"",T))");
    
    assert leftFold((A,(K,V))=>(A++V),"",T)="onetwothree";
    
    logMsg(info,"sub = $(leftFold((-),0,L))");
    logMsg(info,"sub = $(rightFold((-),0,L))");
    
    assert leftFold((-),0,L)=-10;
    
    assert leftFold1((+),L) = 10;
  }
}