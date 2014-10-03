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
arrayUpdates is package{
  -- test list updating
  
  AA has type ref list of integer;
  var AA := iota(1,10,1);
  
  main() do {
    update (X where X%2=0) in AA with -1;
    
    logMsg(info,"AA=$AA");
    
    assert AA=list of [1,-1,3,-1,5,-1,7,-1,9,-1];
    
    delete (X where X<0) in AA;
    
    logMsg(info,"AA is $AA");
    
    assert AA=list of [1,3,5,7,9];
    
    extend AA with -1;
    
    merge AA with list of [-2,-3];
    
    logMsg(info,"AA is now $AA");
    
    assert AA = list of [1,3,5,7,9,-1,-2,-3];
  }
}