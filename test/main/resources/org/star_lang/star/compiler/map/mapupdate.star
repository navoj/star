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
mapupdate is package{
  type props is props{
    values has type ref dictionary of (string,string);
  };
  type person is person{
    name has type string;
    atts has type props;
  };
  
  prc main() do
  {
    var Joe := person{name = "Joe"; atts = props{values:=dictionary of ["alpha"->"beta"] }};
    
    var prop := "alpha";
    
   (Joe.atts.values)[prop] := "gamma";
    
    prop := "one";
    (Joe.atts.values)[prop]:="gamma";
    
    logMsg(info,"$Joe");
    
    assert (Joe.atts.values)[prop] has value "gamma"; 
  }
}