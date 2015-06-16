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
actorCrud is package{
  def A is actor{
    var R := list of [];
  };
  
  
  main() do {
    assert (query A's R with R) = list of [];
    
    request A to extend R with ("peter",1);
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1)];
    
    request A to merge R with list of [("john",2), ("alfred",3)];
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [ ("peter",1), ("john",2), ("alfred",3)];
    
    request A to delete ((_,X) where X>2) in R;
    
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1),("john",2)];
    
    request A to update ((U,V) where V % 2 = 0) in R with (U,V*2);
    logMsg(info,"A's R= $(query A's R with R)");
    assert (query A's R with R) = list of [("peter",1), ("john",4)];
  }
}