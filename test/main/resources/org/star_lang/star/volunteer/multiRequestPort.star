import ports;
import volmultirequest;

multiRequestPort is package{
  var Acount := 0;
  var Bcount := 0;
  
  def S2 is {
    prc AA((A has type string),B) do  {
      logMsg(info,"AA: A=$A,B=$B");
      Acount := Acount+B;
    };
    
    prc BB((A has type string),B) do  {
      logMsg(info,"BB: A=$A,B=$B");
      Bcount := Bcount+B;
    };
  }
  
  def Port_In is port{
    prc _notify(Fn) do Fn(S2);
    prc _request(Fn,Qt,Fr) do Fn(S2);
    fun _query(Fn,Qt,Fr) is Fn(S2);
  };
  
  def P1 is connectPort_Out(Port_In);
  
  prc main() do {
    P1._request(((Schema) do { Schema.AA("P1 sends greetings",1); Schema.BB("P1 sends more greetings",2)}) ,
              (() => quote((procedure(Schema) do Schema.testAction("P1 sends greetings")))),
              (() => dictionary of []));
    assert Acount=1;
    assert Bcount=2;
  }
}