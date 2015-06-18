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
defaultContracts is package{

  contract foo over %t is {
    bar has type (%t)=>string;
    
    fun bar(X) default is __display(X)
  }
  
  implementation foo over integer is {
    fun bar(I) is "%$I";
  }
  
  implementation foo over float is {}
  
  prc main() do {
    logMsg(info,bar(12));
    logMsg(info,bar(12.4));
  }
}