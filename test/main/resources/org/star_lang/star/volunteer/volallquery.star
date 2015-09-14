import volunteers;

volallquery is connections {
  originate(Port_0,{DELETE has type(string) => boolean;
                          Folder has type ref list of ((string, boolean))});
  respond(Port_1,{DELETE has type(string) => boolean;
                  Folder has type ref list of ((string, boolean));
                  report has type action()});
  -- connect(Port_0, Port_1,(volunteer DELETE(x0) as DELETE(x0)));
  connect(Port_0, Port_1,(volunteer query X as X));
}