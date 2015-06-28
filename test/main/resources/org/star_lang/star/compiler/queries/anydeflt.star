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
anydeflt is package{

  type tool is toolData{
    name has type string;
    value has type float
  }
  
  def gold is list of [ toolData{name="alpha"; value=1.0},
                    toolData{name="beta"; value = 2.0} ];
  
  fun getValue(N) is any of V where R in gold and R matches toolData{name=N; value=V};

  prc main() do {
    assert getValue("alpha") has value 1.0;
    
    assert getValue("beta") has value 2.0;
    
    assert getValue("gamma") = none;
    
    def N is "alpha";
    def goldValue is any of V where R in gold and R matches toolData{name=N; value=V};
    assert goldValue has value 1.0;
  }
}
