disjunctives is package{

  def people is list of [ ("john",23), ("peter", 24), ("alice", 22), ("jane", 27) ];
  
  def males is list of [ "john", "peter"];
  
  def females is all W where (W,_) in people and not W in males;
  
  def QQ is all (W,A) where ((W,A) in people and A>25) or ((W,A) in people and W in males) and A<24;
  
  prc main() do {
    logMsg(info,"QQ = $QQ");
    assert QQ=list of [ ("jane",27),("john",23)];
    
    logMsg(info,"F = $females");   
  }
}