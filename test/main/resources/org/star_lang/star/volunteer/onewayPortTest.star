import ports;
import volOnewayRequest;

onewayPortTest is package{

  def S2 is {
    prc testAction((X has type string)) do logMsg(info,"P2:$X");
  }
  
  def Port_In is port{
    prc _notify(Fn) do Fn(S2);
    prc _request(Fn,Qt,Fr) do Fn(S2);
    fun _query(Fn,Qt,Fr) is Fn(S2);
  };
  
 
  def P1 is connectPort_Out(Port_In);
  
  prc main() do {
    P1._request(((Schema) do Schema.testAction("P1 sends greetings")),
              (() => quote((procedure(Schema) do Schema.testAction("P1 sends greetings")))),
              (() => dictionary of []));
  }
}