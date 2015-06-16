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
nonfuncon is package{
  -- a test of non-function contract elements
  
  type Gen of %a is Gen((integer) => %a)

  contract Arb over %a is {
    arb has type Gen of %a;
  }
  
  implementation Arb over string is {
    def arb is Gen((Ix) => display(Ix));
  }
  
  XX has type Gen of string;
  def XX is arb;
  
  xGen has type (Gen of string,integer)=>string;
  fun xGen(Gen(F),Ix) is F(Ix);
  
  prc main() do {    
    assert xGen(arb,3)="3";
  }
}