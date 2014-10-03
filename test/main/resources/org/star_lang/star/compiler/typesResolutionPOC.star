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
typesResolutionPOC is package {

  A has type list of ((string, integer));
  A is list of [("alpha",1), ("beta",2)];
  
  B has type list of ((integer, long));
  B is list of [(1,1L), (2,2L)];
  
  AA is list of [("alpha",1L), ("beta",2L)];
  
  main() do {
    q is all (x, z) where (x, y) in A and (y, z) in B;
   
    logMsg(info,"q=$q"); 
    assert q complement AA=list of [];
  } 
}