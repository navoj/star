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
floatDefltBug is package{

  type result is result {
    name has type string;
    value_A has type float;
    value_B has type float;
    group has type string;
    diff has type float;
    diff default is (value_B-value_A);
    L_spec has type float;
    H_spec has type float;
  };
  
  def testValue is 1.0;
  def goldValue is 1.5;
  
  prc main() do {
    def N is "fred";
    def groupName is "group";
    def lower is 0.0;
    def upper is 1.0;
     
    def R is result{
      name=N;
      value_A=goldValue;
      value_B=testValue;
      group=groupName;
      L_spec=lower;
      H_spec=upper;
      };
    assert R.name=N;
    assert R.diff = testValue-goldValue;
  }
}