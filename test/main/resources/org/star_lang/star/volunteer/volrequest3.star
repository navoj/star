import volunteers;
import ports;
 
volrequest3 is connections {
  originate(p1,{DO has type action(string, integer); ZERO has type action(); ONE has type action(string);THREE has type action(string,integer,string)});
  respond(p2,{OD has type action(string, integer); OREZ has type action(); ENO has type action(string); EERHT has type action(string,string,integer)});
  connect(p1,p2,(volunteer DO(A, B) as OD(A, B)));
  connect(p1,p2,(volunteer ZERO() as OREZ()));
  connect(p1,p2,(volunteer ONE(X) as ENO(X)));
  connect(p1,p2,(volunteer THREE(A, B, C) as EERHT(A, C, B)));
}