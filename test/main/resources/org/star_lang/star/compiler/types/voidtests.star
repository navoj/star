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
voidTests is package {
  
  -- TEST 1
  
  apply_x has type (() => %a) => %a
  fun apply_x(f) is f()

  prc test1() do {
    apply_x((() do nothing)); 
  }
  
  -- TEST 2
  
  type t of %a is t { f has type (integer) => %a }
  
  fun make_t(get_res) is
    t { f = ( (v) => get_res(v)) }
    
  def t0 is make_t(( (i) => 42))
  def t1 is make_t(( (i) do nothing)) 
  def t2 is make_t(( (i) => ()))
  
  fun call_t(tv) is tv.f(13)
  
  prc test2() do {
    ignore call_t(t0); -- ok
    
    call_t(t2);
  }
  
  prc main() do {
    test1();
    test2(); 
  }
}
