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
complexCons is package{
  -- test a complex cons expression
    
  dbl(X) is X+X;
  
  maxx has type (integer)=>integer;
  maxx(X) where X>10 is X;
  maxx(X) default is 10;
  
  L0 is nil;
  
  L1 is cons(id,L0);
  
  L2 is cons(dbl,L1);
  
  L3 is cons(maxx,L2);
  
  assert size(L3)=3;
}