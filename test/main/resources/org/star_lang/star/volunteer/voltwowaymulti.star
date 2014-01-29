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
import volunteers;

voltwowaymulti is connections {
  originate(PortData,{DEFAULT has type stream of string;
  TEST has type(string) => integer});
  respond(PortINPUT,{DATA has type stream of any});
  respond(PortIn,{DEFAULT has type stream of string});
  connect(PortData, PortINPUT,(volunteer X on DEFAULT as X on DATA));
  connect(PortData, PortINPUT,(volunteer TEST(x0) as TEST(x0)));
  connect(PortData, PortIn,(volunteer X on DEFAULT as X on DEFAULT));
  connect(PortData, PortIn,(volunteer TEST(x0) as TEST(x0)));
}