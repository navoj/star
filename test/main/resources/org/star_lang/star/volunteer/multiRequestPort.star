/**
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
import volmultirequest;

multiRequestPort is package{
  var Acount := 0;
  var Bcount := 0;
  
  S2 is {
    AA((A has type string),B) do  {
      logMsg(info,"AA: A=$A,B=$B");
      Acount := Acount+B;
    };
    
    BB((A has type string),B) do  {
      logMsg(info,"BB: A=$A,B=$B");
      Bcount := Bcount+B;
    };
  }
  
  Port_In is port{
    _notify(Fn) do Fn(S2);
    _request(Fn,Qt,Fr) do Fn(S2);
    _query(Fn,Qt,Fr) is Fn(S2);
  };
  
  P1 is connectPort_Out(Port_In);
  
  main() do {
    P1._request((procedure(Schema) do { Schema.AA("P1 sends greetings",1); Schema.BB("P1 sends more greetings",2)}) ,
              (function() is quote((procedure(Schema) do Schema.testAction("P1 sends greetings")))),
              (function() is dictionary of {}));
    assert Acount=1;
    assert Bcount=2;
  }
}