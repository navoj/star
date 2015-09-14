import volunteers;
import ports;

voltwowayrequest is connections {
  originate(p1,{DO has type for all %t such that (%t)=>()});
  respond(p2,{ODP2 has type for all %t such that (%t)=>()});
  respond(p3,{DO has type for all %t such that (%t)=>()});
  connect(p1,p2,(volunteer DO(X) as ODP2(X)));
  connect(p1,p3,(volunteer X as X))
}