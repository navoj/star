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
stocker is package{
  import actors;
  
  type eventTime is alias of long;
  
  stocker has type ()=>actor of{
    tick has type stream of ((float,eventTime));
    avePrice has type (eventTime,eventTime)=>float;
  };
  stocker() is actor{
    on (Price,When) on tick do
      extend prices with (Price,When);

    avePrice(Frm,To) is average(all Pr where (Pr,W) in prices and Frm<=W and W<To);
  } using {
    prices has type ref relation of ((float,eventTime));
    var prices := relation of {};
  };
  
  average(La) is sum(La)/size(La) as float using {
    sum(L) is valof{
      var total := 0.0;
      for P in L do
        total := total+P;
      valis total;
    }
  } 
  
  main() do {
    S is stocker();
    
    for Pr in list of { (10.0, 0L); (10.2,2L); (9.3,4L); (9.5,5L); (9.7,6L); (10.0,8L); (12.2,10L) } do
      notify S with Pr on tick;
    logMsg(info,"price average 2-10: $(query S's avePrice with avePrice(2L,10L))");
  }
}
  