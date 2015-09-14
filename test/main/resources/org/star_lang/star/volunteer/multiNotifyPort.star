import ports;
import volmultinotify;

multiNotifyPort is package{

  var Acount := 0;
  var Bcount := 0;
  
  def Port_In is p0rt{
    on ((A has type string),B) on AA do {
      Acount := Acount+B;
      logMsg(info,"AA: A=$A,B=$B, Acount=$Acount");
    };
    
    on ((A has type string),B) on BB do {
      Bcount := Bcount+B;
      logMsg(info,"BB: A=$A,B=$B, Bcount=$Bcount");
    };
  };

  def P1 is connectPort_Out(Port_In);
  
  prc main() do {
    notify P1 with ("main greeting on AA",1) on AA;
    notify P1 with ("greeting on BB",2) on BB;

    assert Acount=1;
    assert Bcount=2;
  }
}