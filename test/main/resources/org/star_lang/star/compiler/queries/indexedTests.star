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
indexedTests is package{
  -- test various functions of the indexed tables
  
  var R := indexed {
    ("a",1);
    ("b",2);
    ("c",3);
    ("a",4)
  };
  
  var S := indexed of {
    {name="fred"; dept="a"};
    {name="peter"; dept="b"};
  }
  
  main() do {
    logMsg(info,"R=$R");
    logMsg(info,"S=$S");
    
    extend R with ("d",5);
    assert ("d",5) in R;
    assert (all X where ("d",X) in R) = array of {5};
    
    X is _fold(R, (function((U,V),S) is S+V), 0);
    logMsg(info,"X=$X");
    assert X=15; 
  }
}