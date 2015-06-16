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
complexCond is package{

  def II is list of [1,2,3,4,5,6,7,8]
  
  def EE is list of [0,2,4,6,8,10]
  
  prc even(I) do {
    if I in II and I in EE then
      logMsg(info,"$I is an even number");
  }
  
  prc anyEven() do {
    if I in II and I in EE then
      logMsg(info,"we got even number $I");
  }
  
  prc anyOdd() do {
    if I in II and not I in EE then 
      logMsg(info,"we got odd number: $I");
  }
  
  prc evens(I) where I in II and I in EE do 
        logMsg(info,"$I was evens")
   |  evens(I) default do
        logMsg(info,"$I is not evens");
  
  prc main() do {
    even(1);
    even(2);
    even(4);
    even(10);
    even(0);
    
    anyEven();
    
    anyOdd();
    
    evens(1);
    evens(2);
    evens(10);
  }
}