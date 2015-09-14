-- import ports;
import volunteers;
/*
 * set up three ports with two respond and one originate.
 * The two respond each respond to different functions
 */
 
volfunquery is connections {
  originate(Or,{testFun1 has type(string) => integer; testFun2 has type(string) => string});
  respond(R1,{testFun1 has type(string) => integer});
  respond(R2,{testFun2 has type(string) => string});
  connect(Or,R1,(volunteer testFun1(X) as testFun1(X)));
  connect(Or,R2,(volunteer testFun2(X) as testFun2(X)));
}