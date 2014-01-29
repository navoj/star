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
reduce is package {
 contract Reduce over %%f is {
    reducer has type for all a,b such that ((a, b) => b, %%f of a, b) => b;
  };

  implementation Reduce over cons is {
    reducer is reduceCons;
  } using {
    reduceCons(f, nil, b) is b;
    reduceCons(f, cons(head, tail), b) is f(head, reducer(f, tail, b))
    using {
      f1(aa, bb) is reducer(f, aa, bb);
    };
  };
  
  main() do {
    L is cons of {1;2;3;4};
    
    K is reducer((function(X,Y) is X+Y), L, 0);
    
    logMsg(info,"K=$K");
    
    assert K=10;
  }
}