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
dotacc is package{
  import person;
  
  var people := list of [
    someone{ name="fred" },
    someone{ name="peter"},
    someone{ name="jane" }
  ];
  
  def A is actor{
    def Ap is people;
  };
  
  prc main() do {
    def F is list of { all P where P in people and P.name="fred" };
    logMsg(info,"F=$F");
    
    def G is query A with list of { all P where P in people and P.name="fred" };
    logMsg(info,"G=$G");
  }
} 
 