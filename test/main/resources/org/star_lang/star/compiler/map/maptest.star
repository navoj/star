maptest is package{

  var H := dictionary of [ "A"->1, "B"->2, "C"->3, "D"->4 ];
  
  #mget(?H,?P,?D) ==> (__hashGet(H,P) matches some(V) ? V :  D);

  prc main() do
  {
    def BV is H["B"];
    logMsg(info,"get H[B] is $BV");
    assert BV has value 2;
    
    assert H["D"] has value 4;
    
    assert H["D"]  has value  4;
    
    assert not H["E"] has value  _;
    
    try{
      assert someValue(H["E"]) = 1
    } on abort {
      case E do logMsg(info,"We got the exception $E");
    };
    
    H["E"] := 45;
    
    logMsg(info,"$(mget(H,"D", nonInteger))");
    
    logMsg(info,"$(mget(H,"E", nonInteger))");
    
    remove H["A"];
    
    logMsg(info,"$H");
    
    assert not "A" -> _ in H;
    
    if "A"->_ in H then
      logMsg(info,"$H contains A")
    else
      logMsg(info,"$H does not contain A");
      
    if H["A"] has value _ then
      logMsg(info,"$H contains A")
    else
      logMsg(info,"$H does not contain A");
      
    if "D"->_ in H then
      logMsg(info,"$H contains D")
    else
      logMsg(info,"$H does not contain D");
      
    assert "D" -> _ in H;

    for K->V in H do{
	    logMsg(info,"K=$K, V=$V");
    }
  }
}