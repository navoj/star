import volunteers;
import ports;

badreqvol is connections{
originate(Port_102_out1,{calcVal has type action()});
respond(Port_103_in2,{calcVal has type action()});
connect(Port_102_out1, Port_103_in2,(volunteer  X as X));
connect(Port_102_out1, Port_103_in2,(volunteer query X as X));
}

/*  originate(Port_102_out1,{calcVal has type action()});
  respond(Port_103_in2,{calcVal has type action()});
  connect(Port_102_out1, Port_103_in2,(volunteer query X as X));
}*/