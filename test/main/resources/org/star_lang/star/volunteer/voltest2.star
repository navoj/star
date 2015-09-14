import volunteers;
import ports;

voltest2 is connections {
  originate(Ao,{DATA has type occurrence of string});
  respond(Br,{DATA has type occurrence of string});
  connect(Ao,Br,(volunteer X as X));
}
