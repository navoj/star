import volunteers;

volmultirequest is connections {
  originate(Port_Out,{
    AA has type action(string, integer);
    BB has type action(string, integer);
  });
  respond(Port_In,{
    AA has type action(string, integer);
    BB has type action(string, integer);
  });
  connect(Port_Out, Port_In,(volunteer X as X));
}