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
naive is package{
  fun append(nil,X) is X
   |  append(cons(H,T),X) is cons(H,append(T,X))
  
  fun reverse(nil) is nil
   |  reverse(cons(H,T)) is append(reverse(T),cons(H,nil));
  
  fun iot(K,K) is cons(K,nil)
   |  iot(M,X) is cons(M,iot(M+1,X));
  
  fun bench(Count,Run) is valof{
    def LL is iot(1,Count);
    def St is nanos();
    for Ix in iota(1,Run,1) do{
      def RR is reverse(LL);
    }
    def Tm is (nanos()-St)/Run as long;
    def lips is 500000000L*((Count+Count*Count)as long)/Tm;
    valis lips;
  }
  
  prc main() do {
    for C in range(30,300,10) do
      logMsg(info,"bench of $C is $(bench(C,1000))");
  }
}