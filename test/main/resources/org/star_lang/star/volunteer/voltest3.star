import volunteers;
import ports;

voltest3 is connections {
  originate(p1,{DEFAULT has type occurrence of string;
                OTHER has type occurrence of integer;
                TEST has type (string) => integer});
  respond(p2,{DATA has type occurrence of string;
              TEST has type(string) => integer;
              OTHER has type (string) => integer});
  connect(p1,p2,(volunteer X on DEFAULT as X on DATA));
  connect(p1,p2,(volunteer TEST(XX) as OTHER(XX)));
}