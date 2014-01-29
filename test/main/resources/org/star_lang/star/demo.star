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
  -- sample programs to check out the match compiler with
  
  type List of %t is nil or pair(%t, List of %t);
 
  demo2 has type  (List of %s,List of %s) =>List of %s;
  demo2(nil,Y) is Y;
  demo2(XS,nil) is XS;
  demo2(pair(A,B),pair(X,Y)) is pair(A,pair(X,demo2(B,Y)));
 
  chCode has type (integer) =>string;
  chCode(10) is "\n";
  chCode(32) is " ";
  chCode(48) is "0";
  chCode(X) default is "q";
 
} 