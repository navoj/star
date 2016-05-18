strindex is package{
  prc main() do {
    def src is "the quick brown fox jumped over the lazy dog";
    
    assert src[0] has value 0ct;
    
    var dog := src;
    
    assert dog[4] has value 0cq;
    dog[4] := 0c%;
    assert dog[4] has value 0c%;
    
    logMsg(info,"$dog");
    
    assert src[4:9]="quick";
    
    dog[4:findstring(dog," jumped",0)] := "cat";
    
    logMsg(info,"$dog");
    assert dog="the cat jumped over the lazy dog";
  }
}