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
infer is package{
  fun getE(R) is R.e;
  
  def R1 is {e="alpha"; b=23};
  
  def R2 is {b=34};
  
  def R3 is {a="beta"; e="eta"};
  
  type person is someone{ name has type string; age has type integer } or noone;
  
  fun age(R) is R.age;
  
  prc main() do {
    logMsg(info,"R1.e=$(getE(R1))");
    assert getE(R1)="alpha";
    
    -- logMsg(info,"R2.e=$(getE(R2))");
    logMsg(info,"R3.e=$(getE(R3))");
    
    def F is someone{name="fred"; age=23};
    
    logMsg(info,"fred's age is $(age(F))");
    assert age(F)=23;
  }
}