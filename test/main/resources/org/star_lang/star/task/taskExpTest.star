taskExpTest is package{
  import task;
  
  equal has type (integer,integer)=>boolean;
  fun equal(A,B) is valof{
    logMsg(info,"is $A=$B, $(A=B)");
    valis A=B;
  };
  
  fun tt(X) is task{
    def Y is 2;
    valis X+Y;
  }
  
  fun uu(X) is task{
    logMsg(info,"We got $X");
    var Y := 1;
    Y:=Y*2;
    logMsg(info,"Y is $Y");
    logMsg(info,"returning $(X*Y)");
    valis X*Y;
  };
  
  def ww is task {
    var v := 1;
 --   logMsg(info, "v is $v");
    if equal(v,2) then {
      perform task { v := 3; valis () }
    }
 --   logMsg(info, "v is now $v");
    valis v;
  };
  
  prc main() do {
    def XX is valof tt(3);
    assert XX=5;
    
    def YY is valof uu(3);
    assert YY=6;
    
    -- __stop_here();
    assert valof ww = 1;
  }
}