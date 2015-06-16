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
assigntest is package {
  import actors;
  
  A has type actor of{        
    setA has type action(string);
    getA has type ()=>string; 
    m has type ref string;
  };   
  def A is actor {        
    prc setA(a) do { m := a; };
    fun getA() is m;
    var m := "";       
  };
  
  prc main() do {
    request A's setA to setA("hello");
    def m1 is query A's m with m;
    def m3 is query A's getA with getA();
    
    assert m1=m3;  
  }
}