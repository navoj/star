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
ptest1 is package{
  
  def R is list of [("fred",1),("fred",3),("Bob",3)];
  def TM is ((X) from ("fred",X));
  
  prc main() do {
  	
  	if ("fred",3) in R then {
  		logMsg(info, "(\"fred\",3) is in R");  
 	 } else {
  		logMsg(info, "(\"fred\",3) is not in R");  
  	};
  	
  	if TM(3) in R then {
  		logMsg(info, "TM(3) is in R");  
 	 } else {
  		logMsg(info, "TM(3) is not in R");  
  	};
  }
}