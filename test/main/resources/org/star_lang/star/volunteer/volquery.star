import volunteers;
volquery is connections {
  originate(PortOut,{names has type list of string;
                     numbers has type list of string});
  respond(PortIn,{names has type list of string;
                  numbers has type list of string});
  connect(PortOut, PortIn,(volunteer X as X));
  connect(PortOut, PortIn,(volunteer numbers as numbers));
}