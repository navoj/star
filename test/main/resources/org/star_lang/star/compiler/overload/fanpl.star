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
fanpl is package{
  contract A over %t is {
    pl has type (%t,%t)=>%t;
  }
  
  implementation A over integer is {
    pl(X,Y) is X+Y;
  }
  
  implementation A over list of %t where A over %t is {
    pl(A1,A2) is arPlus(A1,A2);
  }
  
  arPlus(list of [],list of []) is list of [];
  arPlus(list of [E1,..L1], list of [E2,..L2]) is list of [pl(E1,E2),..arPlus(L1,L2)];
  
  f(X) is pl(X,X);
  
  g(X) is let{
    h(U) is pl(U,X);
  } in h(X)
  
  main() do {
    assert pl(2,3)=5;
    
    assert f(2)=4;
  
    assert pl(list of [1,2,3], list of [4,5,6]) = list of [5,7,9]
  }
}
