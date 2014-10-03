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
mapOfMaps is package{
  MM has type ref dictionary of (string, ref dictionary of (string,integer));
  var MM := dictionary of {};
  
  main() do {
    MM["a"] := _cell(dictionary of {"b"->0});
    MM["b"] := _cell(dictionary of {})
    
    -- (!MM["a"])["b"] := 1;
    M is MM["a"];
    M["b"] := 1;
    
    assert (!MM["a"])["b"]=1;
    
    N is MM["b"];
    N["c"] := 1;
    -- (!MM["b"])["c"] := 1;
    
    assert (!MM["b"])["c"]=1;
  }
}