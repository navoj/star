subTask is package{
  fun tt(X) is task{
    valis X+2;
  }
  
  fun uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y+valof tt(3);
    valis X+Y;
  };
  
  prc main() do {    
    def YY is valof uu(3);
    assert YY=9;
  }
}
  