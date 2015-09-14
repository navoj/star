sortrel is package{

  def R is list of [(1,2),(3,4),(1,2)];
  def L is sort(R,<);
  
  def BigR is all (Ix,Ix+1) where Ix in (iota(1000,1,-1) has type list of integer);

  prc main() do {
    logMsg(info, "L is $L");
    
--    logMsg(info,"$BigR");
    
    def SortedBigR is sort(BigR,<);
    
--    logMsg(info,"$SortedBigR");
    
    assert SortedBigR = (all (Ix,Ix+1) where Ix in (iota(1,1000,1) has type list of integer));
  };
}