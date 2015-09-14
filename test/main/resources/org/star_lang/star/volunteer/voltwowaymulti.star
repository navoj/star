import ports;
import volunteers;

voltwowaymulti is connections {
  originate(PortData,{DEFAULT has type occurrence of string;
  TEST has type(string) => integer});
  respond(PortINPUT,{DATA has type occurrence of any});
  respond(PortIn,{DEFAULT has type occurrence of string});
  connect(PortData, PortINPUT,(volunteer X on DEFAULT as X on DATA));
  connect(PortData, PortINPUT,(volunteer TEST(x0) as TEST(x0)));
  connect(PortData, PortIn,(volunteer X on DEFAULT as X on DEFAULT));
  connect(PortData, PortIn,(volunteer TEST(x0) as TEST(x0)));
}