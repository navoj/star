regcases is package{
  prc main() do {
    def X is "a string";
    
    switch X in {
      case `.*(s.*i:M).*` do{
        logMsg(info,"got #M");
        assert M="stri"
       }
      case `a string` do
        logMsg(info,"got a string");
      case D default do
        logMsg(info,"default case: $D");
    }
  }
}