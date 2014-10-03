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
assignments is package{

  type person is noone or someone{
    name has type ref string;
    spouse has type ref person;
    spouse default := noone;
  }
  
  type family is family{
    parents has type list of person;
    children has type ref list of person;
    children default := list of [];
  };
  
  var LL := list of [1, 2, 3, 4];
  
  var PP := list of [ someone{name:="a"}, someone{name:="b"}, someone{name:="c"}];
  
  main() do {
    var F := family{ parents=list of [someone{name:="p"}, someone{name:="m"}] };
    
    var X := 0;
    
    assert X=0;
    X := X+1;
    assert X=1;
    
    PP[1].spouse:=someone{name:="d"};
    
    assert PP[1].spouse.name="d";
    
    logMsg(info,"PP is now $PP");
    
    PP[1].spouse.name:="e";
    
    logMsg(info,"PP[1].spouse.name = $(PP[1].spouse.name)");
    assert PP[1].spouse.name="e";
    
    F.children:=list of [someone{name:="cc"}];
    logMsg(info,"F=$F");
  }
}