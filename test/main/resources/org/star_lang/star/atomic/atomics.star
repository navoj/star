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
atomics is package{
  contract assignment over %t determines %v is {
    _atomic has type (%v)=>%t;
    _get has type (%t)=>%v;
    _assign has type action(%t,%v);
  }
  
  implementation assignment over atomic of %t determines %t is {
    fun _atomic(V) is atomic(V);
    fun _get(A) is __atomic_reference(A);
    prc _assign(A,V) do __atomic_assign(A,V);
  }
  
  implementation assignment over atomic_int determines integer is {
    fun _atomic(integer(I)) is atomic_int(I);
    fun _get(A) is integer(__atomic_int_reference(A));
    prc _assign(A,integer(I)) do __atomic_int_assign(A,I);
  }
}