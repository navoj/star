import volunteers;
import ports;

volrequest is connections {
  originate(p1,{DEFAULT has type occurrence of string; TEST has type(string) => integer; DO has type (string)=>()});
  respond(p2,{DATA has type occurrence of any; TRY has type(string) => integer; OD has type (string)=>()});
  connect(p1,p2,(volunteer X on DEFAULT as (X cast any) on DATA));
  connect(p1,p2,(volunteer DO(A) as OD(A)));
  connect(p1,p2,(volunteer TEST(X) as TRY(X)));
}