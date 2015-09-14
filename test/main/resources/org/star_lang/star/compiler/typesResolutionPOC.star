typesResolutionPOC is package {

  A has type list of ((string, integer));
  def A is list of [("alpha",1), ("beta",2)];
  
  B has type list of ((integer, long));
  def B is list of [(1,1L), (2,2L)];
  
  def AA is list of [("alpha",1L), ("beta",2L)];
  
  prc main() do {
    def q is all (x, z) where (x, y) in A and (y, z) in B;
   
    logMsg(info,"q=$q"); 
    assert q complement AA=list of [];
  } 
}