macrolog is package{
  #testIntern(?X) ==> #~#("Apply"#+$$X)#;
  
  prc main() do {
    def Apply2 is 2;
    
    assert testIntern(2)=2
  }
}