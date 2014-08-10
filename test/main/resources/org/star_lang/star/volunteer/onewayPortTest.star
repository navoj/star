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
import volOnewayRequest;

onewayPortTest is package{

  S2 is {
    testAction((X has type string)) do logMsg(info,"P2:$X");
  }
  
  Port_In is port{
    _notify(Fn) do Fn(S2);
    _request(Fn,Qt,Fr) do Fn(S2);
    _query(Fn,Qt,Fr) is Fn(S2);
  };
  
 
  P1 is connectPort_Out(Port_In);
  
  main() do {
    P1._request((procedure(Schema) do Schema.testAction("P1 sends greetings")),
              (function() is quote((procedure(Schema) do Schema.testAction("P1 sends greetings")))),
              (function() is dictionary of {}));
  }
}