macrolog is package{
  #testLog(?X) ==> _macro_log("Apply "++$$X,X);
  
  prc main() do {
    assert testLog(2)=2
  }
}