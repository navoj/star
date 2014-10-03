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
formats is package{
 -- test the various formatting capabilities
 
  main() do {
    val is 123004.564;
    neg is -val;
    
    large is 5.4561e20;
    small is -large;
   
    assert "--$val:P999999.99P;--"="-- 123004.56 --";
    assert "--$neg:P999999.99P;--"="--(123004.56)--";
    
    assert "--$large:+9,900.00e+99;--"="--+5,456.10e+17--";
  }
}