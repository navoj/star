relForLoops is package{
  -- focus on for loops across lists
  
  def R is list of [ (1,"one","alpha"),
                 (2,"two","beta"),
                 (3,"three","gamma"),
                 (4,"four","delta"),
                 (5,"five","eta")];
                 
  prc main() do {
    logMsg(info,"R=$R");
    
    for E in R do{
      logMsg(info,"E=$E");
    };
    
    var count := 0;
    for (3,N,L) in R do{
      logMsg(info,"N=$N, L=$L");
      count := count+1;
    }
    assert count=1;
  }
}
