forLoops is package{
  -- test out various for loops
    
  def L is list of [1, 2, 3, 4, 5]
  
  def C is cons of [1, 2, 3, 4, 5]
    
  def M is dictionary of [1->"1",  2->"2",  3->"3",  4->"4",  5->"5"]
  
  prc main() do {
    -- basic loops
 
    for l in L do
      logMsg(info,"l=$l");
 
    for c in C do
      logMsg(info,"c=$c");
 
    for k->v in M do
      logMsg(info,"k=$k,v=$v");
      
    -- index loops
    
    for Ix->l in L do{
      logMsg(info,"l=$l, Ix=$Ix");
      assert l=Ix+1
    };
    
    for Ix->a in C do{
      logMsg(info,"a=$a, Ix=$Ix");
      assert a=Ix+1
    };
    
    for k->v in M do{
      logMsg(info,"v=$v, k=$k");
    }
  }
}
