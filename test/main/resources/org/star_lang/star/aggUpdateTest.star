aggUpdate is package{
  import person;
  
  P is someone{
    name = "fred";
  };
  
  main() do {
    logMsg(info,"P=$P");
    
    Q is P substitute {name="john"};
    
    logMsg(info,"Q=$Q");
    
    assert Q.name="john";
  }
}