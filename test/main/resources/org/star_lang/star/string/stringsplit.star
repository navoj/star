splitstring is package{

  match has type (string,string) => boolean;
  fun match(K,S) is K in splitString(S,"[|]");
  
  main has type action();
  prc main() do {
    logMsg(info,"splitting on |: $(splitString("BG|BG1|WB|MOLD","\\\\|"))");
    
    logMsg(info,"is foo in bar|foo|bar? $(match("foo","bar|foo|bar"))");
    logMsg(info,"is fob in bar|foo|bar? $(match("fob","bar|foo|bar"))");
    
    assert match("foo","bar|foo|bar");
    
    assert not match("fob","bar|foo|bar");
  }
} 