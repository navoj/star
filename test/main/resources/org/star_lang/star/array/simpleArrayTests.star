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
simpleArrayTests is package{
  -- base test of array functions.
  
  main() do {
    -- create an array using the sequence notation:
    A is array of {"alpha"; "beta"; "gamma"};
    
    assert size(A)=3;
    assert A=array of {"alpha"; "beta"; "gamma"};
    
    assert A!=array of {};
    assert A!=array of {"alpha"; "better"; "gammer"};
    
    logMsg(info,"A is $A");
    -- assert display(A)="array of {\"alpha\";\"beta\";\"gamma\"}"; -- cancel this until overloading bug wrt default implementation is resolved.
    
    walkOver(A);
    walkBack(A);
    
    L has type array of long;
    L is iota(1L,10L,1L);
    assert L=array of {1L; 2L; 3L; 4L; 5L; 6L; 7L; 8L; 9L; 10L};
    
    F has type array of float;
    F is iota(5.0,1.0,-1.0);
    assert F=array of {5.0; 4.0; 3.0; 2.0; 1.0};
  }
  
  walkOver(sequence of {}) do nothing;
  walkOver(sequence of {H;..T}) do {
    logMsg(info,"got $H");
    walkOver(T);
  };
    
  walkBack(sequence of {}) do nothing;
  walkBack(sequence of {T..;H}) do {
    logMsg(info,"got $H");
    walkBack(T);
  }
}