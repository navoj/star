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
reduceC is package{
  contract reduce over %t determines %a is {
    reducer has type for all b such that ((%a,b)=>b) => ((%t, b)=>b);
    reducel has type for all b such that ((b,%a)=>b) => ((b, %t)=>b);
  }
 
  implementation reduce over cons of %t determines %t is {
    fun reducer(f) is (x,z) => foldr(f,z,x);
    fun reducel(f) is (x,z) => foldl(f,x,z);
  }
  
  fun foldr(f,z,nil) is z
   |  foldr(f,z,cons(x,xs)) is f(x,foldr(f,z,xs))
  
  fun foldl(f,z,nil) is z
   |  foldl(f,z,cons(x,xs)) is foldl(f,f(z,x),xs)
}