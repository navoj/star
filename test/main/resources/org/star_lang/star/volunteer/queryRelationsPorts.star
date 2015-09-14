import ports;
import voltestrels;
import user;

queryRelationsPorts is package{
  
  def Port_In is p0rt{
    on U on DEFAULT do addUser(U);
    
    Users has type ref list of user;
    var Users := list of [];
    
--    addUser has type action(user);
    prc addUser(U) do{
      logMsg(info,"New user: $U");
      extend Users with U;
    };
    
    prc calcTotal() do
      logMsg(info,"total balance is $(total(list of { all U.balance where U in Users} ))");
       
    fun getBalance(N) is someValue(any of B where user{name=N;balance=B} in Users);
  };
  
  fun total(R) is valof{
    var T := 0;
    for r in R do
      T := T+r;
    valis T;
  };

  def P1 is connectPort_Out(Port_In);
  
  prc main() do {
    notify P1 with user{name="alpha";balance=1} on DEFAULT;
    notify P1 with user{name="beta";balance=2} on DEFAULT;
    notify P1 with user{name="gamma";balance=3} on DEFAULT;
    request P1's addUser to addUser(user{name="delta";balance=4});
    
    def U is user{name="fred"; balance=5};
    request P1 to extend Users with U;

    logMsg(info,"$(query P1's Users with Users)");
    assert (query P1's Users with Users) = list of[ user{name="alpha";balance=1},
        user{name="beta";balance=2},
        user{name="gamma";balance=3},
        user{name="delta";balance=4},
        user{name="fred"; balance=5} ];
        
    assert (query P1's Users with any of U where U in Users and U.name="fred") has value user{name="fred";balance=5}
    
    request P1's calcTotal to calcTotal();
  }
}