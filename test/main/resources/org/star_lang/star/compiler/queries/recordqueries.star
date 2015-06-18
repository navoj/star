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
recordqueries is package{

  type gender is male or female;
  
  type foo is foo{
    name has type string;
    gender has type gender;
    add has type address;
  }
  
  type address is street{
    street has type string;
    unit has type integer;
    city has type string;
  };
  
  persons has type ref list of foo;
  
  var persons := list of [ foo{name="alpha"; gender=male; add=street{ street="main st"; unit=1; city="my city"}},
                           foo{name="beta"; gender=female; add=street{street="union st"; unit=100; city="ny"}},
                           foo{name="gamma"; gender=male; add=street{street="water st"; unit=110; city="ny"}}];
                                   
  prc main() do {
    assert foo{name="alpha"} in persons;
    
    logMsg(info,"$(all P where P in persons and P.name>="alpha" order by P.add.unit)");
  }
}
                              
                                   