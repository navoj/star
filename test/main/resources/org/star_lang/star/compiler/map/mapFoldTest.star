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
mapFoldTest is package{
  def H is dictionary of { "A"->1; "B"->2; "C"->3; "D"->4 };
  
  def L is leftFold((A,(K,V))=>(A+V),0,H);
  def R is rightFold(((K,V),A)=>(A*V),1,H);
  
  prc main() do {
    assert L=10;
    assert R=24;
  }
}
 