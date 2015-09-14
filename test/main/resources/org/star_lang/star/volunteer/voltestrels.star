import volunteers;
import ports;
import user;

voltestrels is connections {
  originate(Port_Out,{DEFAULT has type occurrence of user;
    Users has type ref list of user;
    calcTotal has type action();
    addUser has type action(user);
    getBalance has type(string) => integer});
  respond(Port_In,{DEFAULT has type occurrence of user;
    Users has type ref list of user;
    calcTotal has type action();
    addUser has type action(user);
    getBalance has type(string) => integer});
  connect(Port_Out, Port_In,(volunteer X on DEFAULT as X on DEFAULT));
  connect(Port_Out, Port_In,(volunteer query X as X));
  connect(Port_Out, Port_In,(volunteer request X as X));
}