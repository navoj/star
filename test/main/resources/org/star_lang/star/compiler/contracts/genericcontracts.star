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
genericcontracts is package{
  import random;
  
  main() do {
    G is StdGen(0,1000);
    
    (R1,G1) is next(G);
    
    logMsg(info,"first random is $R1");
    
    (R2,G2) is next(G1);
    
    logMsg(info,"second is $R2");
    
    -- only pseudo random
    assert R1=2106791562;
    assert R2=2018320191;
  }
  
}