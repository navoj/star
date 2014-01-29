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
-- A simple test of fluent relations
fluents is package{

  var fl := fluent{("alpha",1) at 1; ("beta",2) at 1~~4; ("gamma",3) at ~~4; ("beta",4) at 1};
  
  main has type action();
  main() do {
    logMsg(info,"Fluent fl is $fl");
    
    -- Look for ("beta",_) at 2~~3
    assert ("beta",_) at 2~~3 in fl;
    
    -- look for current values
    for current T in fl do
      logMsg(info,"current $T");
    
    -- look for current values that are one or after one
    for T at 1 in fl do
      logMsg(info,"$T at 1");
      
    -- add a bunch of entries
    for ix in iota(1,100,1) do
      initiate ("$ix",ix) at ix~~(ix+10) in fl;
    
    logMsg(info,"fl = $fl");
    
    -- forget some
    forget fl before 40;
    
    for ix in iota(1,120,1) do
      logMsg(info,"$(all T where T at ix in fl)");
  }
}
    
    