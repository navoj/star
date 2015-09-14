importTest is package{
  import idType;
  
  type anotherType is another{
    id has type Id; -- from idType;
  }
  
  prc main() do {
    def XX is another{id=Id("fred")}
   
    assert XX.id = Id("fred")
    logMsg(info,"$XX");
  }
}