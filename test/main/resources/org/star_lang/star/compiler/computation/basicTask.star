basicTask is package{
  -- import tasking;
   
  fun tt(X) is task{
    def Y is 2;
    valis X+Y;
  }
  
  fun uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y+2;
    valis X+Y;
  };
  
  prc main() do {
    def XX is valof tt(3);
    assert XX=5;
    
    def YY is valof uu(3);
    assert YY=6;
  }
}
  