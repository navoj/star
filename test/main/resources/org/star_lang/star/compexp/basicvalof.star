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
basicvalof is package{
  -- test basic computation expression
  import execution;
  
  type value of %t is value(%t);
  
  implementation execution over value is {
    _zero() is value(unit);
    _bind(value(X),F) is F(X);
    _combine(X,F) is F(X);
    _return(X) is value(X);
    _delay(F) is F();
    _run(value(X)) is X;  
  };
  
  main() do {
  
    -- try out the contract directly
    A0 has type value of integer;
    A0 is _return(3);
    logMsg(info,"A0=$A0");
    assert A0 = value(3);
    
    A1 is _delay((function() is A0));
    logMsg(info,"A1=$A1");
    
    A2 is _run(A1);
    logMsg(info,"A2=$A2");
    
    B0 is _run(_delay((function() is (_return(3) has type value of integer))));
    logMsg(info,"B0=$B0");
    assert B0=3;
    
    Z is value execute {
      return 3
    };
    assert Z=3;
    
    T is value execute {
      bind A to value(1);
      B is 2;
      
      return A+B
    }
    
    assert T=3
  }
} 