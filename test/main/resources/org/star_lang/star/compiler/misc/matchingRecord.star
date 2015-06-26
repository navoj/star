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
matchingRecord is package{
  type foo is foo{
    X has type integer;
    Y has type string;
  }
  
  fun hasY(X matching foo{Y=S}) is S;
  
  -- findK(L,K) where R matching foo{X=K} in L is R;
  
  fun findK(K,cons(H matching foo{X=K},T)) is H
   |  findK(K,cons(_,T)) is findK(K,T)
  
  prc main() do {
    assert hasY(foo{X=23;Y="fred"})="fred";
    
    assert findK(2,cons of [foo{X=1;Y="a"}, foo{X=2;Y="b"}, foo{X=3;Y="c"}]) = foo{X=2;Y="b"};
  }
} 