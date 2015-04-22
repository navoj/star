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
arrayIndex is package{
  -- test indexing into arrays
  
  main() do {
    II has type ref list of string;
    var II := list of ["alpha", "beta", "gamma", "delta"];
    
    assert II[0] has value "alpha";
    assert II[1] has value "beta";
    assert II[2] has value "gamma";
    assert II[3] has value "delta";
    
    assert (II[4] or else nonString)=nonString;
    assert (II[-1] or else nonString)=nonString;
    
    var C := list of [0,1,2];
    
    C[1] := 4;
    assert C=list of [0,4,2];
  }
}