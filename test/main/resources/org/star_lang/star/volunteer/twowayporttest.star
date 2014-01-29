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
import voltwowayrequest;
import ports;

twowayporttest is package{

  P2 is p0rt{
    on X on ODP2 do logMsg(info,"P2:$(X cast any)");
  };   
  
  P3 is p0rt{
    on X on DO do logMsg(info,"P3:$(X cast any)");
  };
  
  P1 is connectp1(P2,P3);
  
  main() do {
    request P1's DO to DO("P1 sends greetings");
  }
}