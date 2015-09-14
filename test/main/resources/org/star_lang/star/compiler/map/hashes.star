hashes is package{
  -- trial data
  
  def M is dictionary of ["a"->1, "b"->2, "c"->3];
  
  prc main() do {
    logMsg(info,"M=$M");
    -- test indexable
    assert M["a"] has value 1;
    assert M["b"] has value 2;
    assert M["c"] has value 3;
    
    assert not M["d"] has value _;
    
    -- test sizeable
    assert size(M)=3;
    assert not isEmpty(M);
    
    -- test pPrint
    logMsg(info,"Showing M: $M");
    
    -- test iterate
    def FF is _iterate(M,(V,ContinueWith(SS)) => ContinueWith(V+SS),ContinueWith(0));
    assert FF=ContinueWith(6);
  }
}