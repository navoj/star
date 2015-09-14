strindex is package{
  prc main() do {
    def src is "the quick brown fox jumped over the lazy dog";
    
    assert src[0] has value 't';
    
    var dog := src;
    
    assert dog[4] has value 'q';
    dog[4] := '%';
    assert dog[4] has value '%';
    
    logMsg(info,"$dog");
    
    assert src[4:9]="quick";
    
    dog[4:findstring(dog," jumped",0)] := "cat";
    
    logMsg(info,"$dog");
    assert dog="the cat jumped over the lazy dog";
  }
}