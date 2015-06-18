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
stringParse is package{
  def A is 1;
  def B is "3";
  
  def Cheese is 23;
  def Ham is 21;
  
  fun priceOf("SKU23") is 34.23
   |  priceOf("SKU21") is 12.00
  
  prc assertEqual(X,Y) do {
    if X!=Y then{
      logMsg(info,"expecting $X, got $Y");
      assert false;
    }
  } 
  
  prc main() do {
    logMsg(info,"C:\\B");
    logMsg(info,"A string$((A as string)++("3\$\n"))B$B");
    
    assertEqual("A string13\$\nB3","A string#((A as string)++("3\$\n"))B#B");
    
    logMsg(info,"price of Cheese is $(priceOf("SKU$Cheese"))");
    
    assert priceOf("SKU$Cheese")=34.23;
  }
}