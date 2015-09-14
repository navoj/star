coerce is package{

  prc main() do {
    def I is "123" as integer;
    
    logMsg(info,"I is $I");
  
    assert I=123;
    
    assert (34.2 as integer)=34;
    
  }
}