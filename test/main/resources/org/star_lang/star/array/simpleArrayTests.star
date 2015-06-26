/**
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
  -- base test of list functions.
  
  prc main() do {
    -- create an list using the sequence notation:
    def A is list of ["alpha", "beta", "gamma"];
    
    assert size(A)=3;
    assert A=list of ["alpha", "beta", "gamma"];
    
    assert A!=list of [];
    assert A!=list of ["alpha", "better", "gammer"];
    
    logMsg(info,"A is $A");
    assert display(A)="list of [\"alpha\", \"beta\", \"gamma\"]"; -- cancel this until overloading bug wrt default implementation is resolved.
    
    walkOver(A);
    walkBack(A);
    
    L has type list of long;
    def L is iota(1L,10L,1L);
    assert L=list of [1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L];
    
    F has type list of float;
    def F is iota(5.0,1.0,-1.0);
    assert F=list of [5.0, 4.0, 3.0, 2.0, 1.0];
  }
  
  prc walkOver([]) do nothing
   |  walkOver([H,..T]) do {
        logMsg(info,"got $H");
        walkOver(T);
      };
    
  prc walkBack([]) do nothing
   |  walkBack([T..,H]) do {
        logMsg(info,"got $H");
        walkBack(T);
      }
}