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
dependcon is package{
  -- test simple contract with dependency
  
  contract Iter over %s determines %e is {
    iter has type for all %a such that (%s,(%e,%a)=>%a,%a)=>%a;
  }
  
  implementation Iter over cons of %e determines %e is {
    fun iter(nil,_,S) is S
     |  iter(cons(H,T),F,S) is iter(T,F,F(H,S))
  }
}