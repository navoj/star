import volunteers;
import ports;

volmultinotify is connections {
  originate(Port_Out,{
    AA has type occurrence of ((string, integer));
    BB has type occurrence of ((string, integer));
  });
  respond(Port_In,{
    AA has type occurrence of ((string, integer));
    BB has type occurrence of ((string, integer));
  });
  connect(Port_Out, Port_In,(volunteer X as X));
}