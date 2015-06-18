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
queryref is package{
  type pp is pp{
    id has type string;
    value has type ref string;
    age has type ref integer;
  };
  
  def LL is list of {pp{id="1"; value:="alpha"; age:=0}; pp{id="2"; value:="beta"; age:=1}; pp{id="3"; value:="gamma"; age:=2}};
  
  prc main() do {
    logMsg(info,"LL=$LL");
    
    def XX is all X.value where X in LL and X.age<2;
    
    logMsg(info,"XX=$XX");
    assert XX = list of {"alpha";"beta"}
  }
} 