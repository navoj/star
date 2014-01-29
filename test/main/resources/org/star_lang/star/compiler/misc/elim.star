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
elim is package{
    
  elim has type (%t)=>%t where equality over %e 'n sequence over %t determines %e;

  elim(sequence of {}) is sequence of {};
  elim(sequence of {H;H;..T}) is elim(sequence of {H;..T});
  elim(sequence of {H;..T}) default is sequence of {H;..elim(T)};
    
  
     
  main() do {
    L1 is list{1;2;9;5;2;2};
    L2 is list{3;3;9;9;0;1;1};
       
    logMsg(info,"elim(L1) is $(elim(L1) has type list of integer)");
    logMsg(info,"elim(L2) is $(elim(L2) has type list of integer)");
       
    assert elim(L1)=list{1;2;9;5;2};
    assert elim(L2)=list{3;9;0;1};
   }
 }