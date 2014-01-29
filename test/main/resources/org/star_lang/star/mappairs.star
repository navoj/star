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
demo is package{
  -- sample program to check out the match compiler with
  
  type List of %t is empty or pair(%t, List of %t);
 
  mappairs has type (((%s,%t) =>%u),List of %s,List of %t) =>List of %u;
  mappairs(F,empty,_) is empty;
  mappairs(F,pair(H,T),empty) is empty;
  mappairs(F,pair(H,T),pair(A,B)) is pair(F(H,A),mappairs(F,T,B));
  
} 