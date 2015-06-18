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
disjunctives is package{

  def people is list of [ ("john",23), ("peter", 24), ("alice", 22), ("jane", 27) ];
  
  def males is list of [ "john", "peter"];
  
  def females is all W where (W,_) in people and not W in males;
  
  def QQ is all (W,A) where ((W,A) in people and A>25) or ((W,A) in people and W in males) and A<24;
  
  prc main() do {
    logMsg(info,"QQ = $QQ");
    assert QQ=list of [ ("jane",27),("john",23)];
    
    logMsg(info,"F = $females");   
  }
}