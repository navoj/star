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
arith1 is package {
  mSum has type (integer)=>integer;
  mPsum has type (integer)=>integer;
  
  fun mPsum(x) where x=1 is 10
   |  mPsum(x) is x * ((mPsum(1)+ x)/2)

  fun mSum(x) is x/2 * mPsum(1) + mPsum(x)
  fun mSum2(x) is  ((x/2) * mPsum(1)) + mPsum(x)
  
  prc main() do {
    var time := nanos();
    var p:=mSum(35);
    var p2:=mSum2(35)
    
    time := nanos()-time;
    logMsg(info,"arith1(35)=$(p) in $(time as float/1.0e9) seconds, $(time) nanos");
    assert p=p2;
    assert p=940;  
  }
}