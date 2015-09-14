sequencePtns is package{
  -- test out the use of <lbl> of [<el> , .. , <el>] patterns
  
  fun find([],_) is false
   |  find([X,.._],X) is true
   |  find([_,..Y],X) is find(Y,X)
  
  fun atEnd(cons of [],_) is false
   |  atEnd(cons of [X],X) is true
   |  atEnd(cons of [_,..Y],X) is atEnd(Y,X)
  
  def L is cons of ["alpha", "beta", "gamma" ]
  
  prc walk(cons of [X,..Y]) do {
      logMsg(info,"got $X");
      walk(Y);
    }
   |  walk(cons of []) do {};
  
  prc main() do {
    logMsg(info,"L=$L");
    assert find(L,"alpha");
    assert find(L,"beta");
    assert find(L,"gamma");
    
    assert not find(L,"one");
    
    assert atEnd(L,"gamma");
    assert not atEnd(L,"alpha");
    assert not atEnd(L,"beta");
    assert not atEnd(L,"one");
    
    walk(L);
  }
}