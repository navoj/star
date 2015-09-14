import volunteers;
import ports;

voltwoway is connections {
  originate(p1,{DEFAULT has type occurrence of string;
                TEST has type(string) => integer});
  respond(p2,{DATA has type occurrence of string;
              TEST has type(string) => integer});
  respond(p3,{DEFAULT has type occurrence of string});
  connect(p1,p2,(volunteer X on DEFAULT as X on DATA));
  connect(p1,p3,(volunteer notify X as X))
}