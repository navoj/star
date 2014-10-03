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
fib is package{
  fib(0) is 1;
  fib(1) is 1;
  fib(N) is fib(N-1)+fib(N-2);
  
  nfib(0) is 1;
  nfib(1) is 1;
  nfib(N) is nfib(N-1)+nfib(N-2)+1;
  
  ifib(0) is 1;
  ifib(1) is 1;
  ifib(N) is valof{
    var prevPrev := 0;
    var prev := 1;
    var result := 0;
    
    for ix in iota(2,N,1) do{
      result := prev+prevPrev;
      prevPrev := prev;
      prev := result;
    }
    
    valis result
  }
}  