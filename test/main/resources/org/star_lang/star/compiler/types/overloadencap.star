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
overloadencap is package{
  -- test some overloading resolution in the context of encapsulated types
  
  type combo of %t is combo{
    t1 has kind type;
    t2 has kind type;
    ast1 has type (%t)=>t1;
    ast2 has type (%t)=>t2;
    cmp has type (t1,t2)=>boolean;
  };
  
  less has type (integer,integer)=>boolean;
  less(X,Y) is X<Y;
  
  main() do{
    C is combo{
      type integer counts as t1;
      type integer counts as t2;
      ast1(X) is X;
      ast2(X) is X;
      cmp has type (integer,integer)=>boolean;
      cmp is (<);
    }
    
    assert C.cmp(C.ast1(1),C.ast2(2));
  }
} 