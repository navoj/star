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
mathLibTest is package{
  private import math;
  
  -- test accessing fibonacci and factorial
  
  main() do {
    var time := nanos();
    res is nfib(24) as long;
    time := nanos()-time;
    logMsg(info,"nfib(24)=$(res) in $(time as float/1.0e9) seconds, $(time/res) nanos/call");
    
    assert res=150049L;
    
    f is fact(10.0);
    logMsg(info,"fact(10.0) is $f");
    
    assert f = 3628800.0;
  }
}