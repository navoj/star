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
safeCast is package{
  -- test safe casting
  
  type group is group{
    elem has kind type where equality over elem;
    
    zero has type elem;
    inv has type (elem)=>elem;
    op has type (elem,elem)=>elem;
    
    eq has type (elem,elem)=>boolean;
    
    el has type for all %t such that (%t)=>elem;
  };
  
  def G is group{
    type integer counts as elem;
    
    def zero is 0;
    fun inv(X) is -X;
    fun op(X,Y) is X+Y;
    
    fun el(X) is X cast elem;
    
    fun eq(X,Y) is X=Y;
  }
  
  prc main() do {
    def Z is G.op(3 cast G.elem,2 cast G.elem);
    
    -- assert G.eq(Z,5 cast G.elem)
    
    assert Z=5 cast G.elem
  }
}