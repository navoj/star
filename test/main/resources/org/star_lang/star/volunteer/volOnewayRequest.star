import volunteers;
import ports;

volOnewayRequest is connections {
  originate(Port_Out,{testAction has type action(string)});
  respond(Port_In,{testAction has type action(string)});
  connect(Port_Out,Port_In,(volunteer testAction(X) as testAction(X)));
}