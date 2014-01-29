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
arrayIndexMatch is package{
  A is array of { "alpha"; "beta"; "gamma"; "delta" };
  
  B is cons of { "alpha"; "beta"; "gamma"; "delta" };
  
  consIndex(L[Ix]) from (L,Ix) where Ix>=0 and Ix<size(L);
  
  main() do {
  	assert (A,0) matches __array_index(E);
  	
  	if (A,0) matches __array_index(E) then
  	  logMsg(info,E);
  	  
  	assert (A,0) matches __array_index(E) and E matches "alpha";
  	
  	assert not (A,4) matches __array_index(E);
  	
  	assert (B,1) matches consIndex("beta");
  	
  	assert not (B,4) matches consIndex(_);
  }
}