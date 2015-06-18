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
deserialmap is package{
  def Src is "alpha => 1.2; beta => 2.4; gamma => 3.6; delta=>4.2";
  
  prc main() do {
    def M is list of { (Key,Value as float) where
             S in splitString(Src,"; *") and
             S matches `(\w+:Key) *=> *(\d+[.]\d+:Value) *` }
    logMsg(info,"M=$M");
    def MM is list of [("alpha", 1.2), ("beta", 2.4), ("gamma", 3.6), ("delta", 4.2)];
    assert M=MM;
  }
}