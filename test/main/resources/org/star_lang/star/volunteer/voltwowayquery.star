import ports;
import volunteers;

voltwowayquery is connections {
originate(Port_Out,{testAction has type(string) => integer});
respond(Port_In,{testAction has type(string) => integer});
connect(Port_Out,Port_In,(volunteer testAction(X) as testAction(X)));
}