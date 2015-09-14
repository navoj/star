whereQuery is package{
  fun foo(rs) is (r in rs where r > 0) ? some(r) : none;
  
  prc main() do {
    logMsg(info,"present = $(foo(list of [-1,0,2]))");
    
    assert foo(list of [-1,0,2]) = some(2);
    
    assert foo(list of [-1,0,0]) = none;
  }
}