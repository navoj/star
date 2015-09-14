actorCrud is package{
  def A is actor{
    var R := list of [];
  };
  
  
  prc main() do {
    assert (query A's R with R) = list of [];
    
    request A to extend R with ("peter",1);
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1)];
    
    request A to merge R with list of [("john",2), ("alfred",3)];
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [ ("peter",1), ("john",2), ("alfred",3)];
    
    request A to delete ((_,X) where X>2) in R;
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1),("john",2)];
    
    request A to update ((U,V) where V % 2 = 0) in R with (U,V*2);
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1), ("john",4)];
  }
}