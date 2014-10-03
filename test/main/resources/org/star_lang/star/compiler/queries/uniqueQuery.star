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
uniqueQuery is package{
  main() do {
    var l:=list of [12,113,8,45,113];
   
    m is 3 of X where X in l order by X;
  
   logMsg(info,"m=$m");
   assert m = list of [8, 12, 45];
  
   q is all X where X in l;
   logMsg(info,"q=$q");
   
   assert q=list of [12,113,8,45,113];
  
   p is unique X where X in l;
   logMsg(info,"p=$p");
   assert p=list of [12, 113, 8, 45];
  }
}