/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
import ports;
import voltestrels;
import user;

queryRelationsPorts is package{
  
  Port_In is p0rt{
    on U on DEFAULT do addUser(U);
    
    Users has type ref list of user;
    var Users := list of [];
    
--    addUser has type action(user);
    addUser(U) do{
      logMsg(info,"New user: $U");
      extend Users with U;
    };
    
    calcTotal() do
      logMsg(info,"total balance is $(total(all U.balance where U in Users))");
       
    getBalance(N) is anyof B where user{name=N;balance=B} in Users;
  };
  
  total(R) is valof{
    var T := 0;
    for r in R do
      T := T+r;
    valis T;
  };

  P1 is connectPort_Out(Port_In);
  
  main() do {
    notify P1 with user{name="alpha";balance=1} on DEFAULT;
    notify P1 with user{name="beta";balance=2} on DEFAULT;
    notify P1 with user{name="gamma";balance=3} on DEFAULT;
    request P1's addUser to addUser(user{name="delta";balance=4});
    
    U is user{name="fred"; balance=5};
    request P1 to extend Users with U;

    logMsg(info,"$(query P1's Users with Users)");
    assert (query P1's Users with Users) = list of[ user{name="alpha";balance=1},
        user{name="beta";balance=2},
        user{name="gamma";balance=3},
        user{name="delta";balance=4},
        user{name="fred"; balance=5} ];
        
    assert (query P1's Users with any of U where U in Users and U.name="fred") = user{name="fred";balance=5}
    
    request P1's calcTotal to calcTotal();
  }
}