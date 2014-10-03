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
castTest is package{
  intervalRel is list of [1.2, 1.3, 3.0, 4.0];
  
  result is ((FSUM(intervalRel) cast double)/size(intervalRel));
            
  FSUM has type (relation of float) => float
  FSUM(rel) is FSUM_HELPER(sort(rel,(function(x,y) is false)), 0);

  FSUM_HELPER has type (list of float, float) => float
  FSUM_HELPER([], sum) is sum;
  FSUM_HELPER([qty]++qtys, sum) is FSUM_HELPER(qtys, (sum+qty));
  
  main() do {
    logMsg(info,"result = $result");
  }
}