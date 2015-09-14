import volunteers;
import ports;

voltwowayrequest2 is connections {
  originate(p1,{DO has type action(string)});
  respond(p2,{ODP2 has type action(string)});
  respond(p3,{ODP3 has type action(string)});
  connect(p1,p2,(volunteer DO(X) as ODP2(X)));
  connect(p1,p3,(volunteer DO(X) as ODP3(X)))
}