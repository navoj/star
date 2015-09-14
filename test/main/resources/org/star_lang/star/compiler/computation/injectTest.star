injectTest is package{
  import task;
  
  fun aa(X) is action computation{
      valis X+2;
  }

  fun tt(X) is task{
    valis valof aa(X);
  }
  
  fun uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y+valof tt(3);
    logMsg(info,"Y is $Y");
    valis X+Y;
  };
  
  prc main() do {    
    def YY is valof uu(3);
    assert YY=9;
  }
}