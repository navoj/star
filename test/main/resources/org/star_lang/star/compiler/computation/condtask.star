condtask is package{
  fun tt(X) is task{
    valis X+2;
  }
   
  fun uu(X) is task{
 --   logMsg(info,"We got $X");
    var Y := 1;

    if X<valof tt(2) then
--      logMsg(info,"adding 2 to Y");
      Y:=Y+2;
    
--    logMsg(info,"Y=$Y");
    valis X+Y;
  };
  
  prc main() do {
    def YY is valof uu(3);
    logMsg(info,"YY=$YY");
    assert YY=6;
  }
}
  