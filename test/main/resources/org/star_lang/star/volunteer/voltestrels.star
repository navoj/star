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
import volunteers;
import ports;
import user;

voltestrels is connections {
  originate(Port_Out,{DEFAULT has type stream of user;
    Users has type ref list of user;
    calcTotal has type action();
    addUser has type action(user);
    getBalance has type(string) => integer});
  respond(Port_In,{DEFAULT has type stream of user;
    Users has type ref list of user;
    calcTotal has type action();
    addUser has type action(user);
    getBalance has type(string) => integer});
  connect(Port_Out, Port_In,(volunteer X on DEFAULT as X on DEFAULT));
  connect(Port_Out, Port_In,(volunteer query X as X));
  connect(Port_Out, Port_In,(volunteer request X as X));
}