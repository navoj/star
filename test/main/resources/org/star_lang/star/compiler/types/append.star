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
append is package{
  -- test simple type inference
  
  append has type (cons of %t, cons of %t) => cons of %t;
  append(nil,X) is X;
  append(cons(H,T),X) is cons(H,appR(T,X));
  
  appR(nil,X) is X;
  appR(cons(H,T),X) is cons(H,append(T,X));
  
  main() do {
    assert append(cons of {1;2;3},cons of {4;5;6}) = cons of {1;2;3;4;5;6}
    
    assert append(cons of {"a";"b"}, cons of {"c"}) = cons of {"a"; "b"; "c"}
  }
}