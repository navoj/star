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
arraysets is package{
  -- test set operations over arrays
  
  def A is list of ["alpha", "beta", "gamma"];
  
  def B is list of ["alpha", "gamma", "delta"];
  
  prc main() do {
    assert A union A=A;
    
    assert B intersect B=B;
    
    assert A complement A = list of [];
    
    assert size(A union B)=4;
    assert size(A intersect B)=2;
    
    assert A complement B=list of ["beta"];
  }
}