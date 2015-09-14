complexkey is package{
  type foo of %t is nf or foo(%t);
  
  prc main() do {
    var K := dictionary of [];
    
    K[foo(3)] := "aleph";
    assert K[foo(3)] has value "aleph"
    
    K[foo(4)] := "beta";
    assert K[foo(3)] has value "aleph"
    assert K[foo(4)] has value "beta"
    
    logMsg(info,"$K");
    
    K[foo(3)] := "garb";
    
    logMsg(info,"$K");
    assert K[foo(3)] has value "garb"
  }
}