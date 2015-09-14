import volunteers;
import ports;
 
volrequest2 is connections {
  originate(p1,{DO has type action(string, integer)});
  respond(p2,{OD has type action(string, integer)});
  connect(p1,p2,(volunteer DO(A, B) as OD(A, B)));
}