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
listEx is package{
  -- test out example cons expressions
  
  prc main() do {
    def L0 is cons of {};
    
    def L1 is cons of {1};
    
    def L2 is cons of {1;2;3;4};
    
    def L3 is cons of {-1;-2;..L2}
    
    def L4 is cons of {L2..;5;6;7}
    
    def L5 is cons of {-2;-1;..L2..;5;6;7}
    
    logMsg(info,"L5=$L5");
    assert L5=cons of {-2;-1;1;2;3;4;5;6;7};
  }
}