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
import colonMacros;

colonMacroTest is package{

  fun Average(Tm) is (Buff) => sum(Buff)/size(Buff);
  
  fun StdDev(Tm) is let{
    fun stdDev(Buff) is valof{
      def Count is size(Buff);
      def M is sum(Buff)/Count;
      valis sqrt(sumSq(Buff)/Count);
    }
  } in stdDev
  
  fun sum(L) is foldF(L,(A,B) => A+B,0)
  
  fun sumSq(L) is foldF(L,(A,B) => A*A+B,0)
  
  foldF has type (list of %e,(%e,%x)=>%x,%x)=>%x
  fun foldF(L,F,I) is valof{
    var XX := I;
    for E in L do
      XX := F(E,XX);
    valis XX;
  }

  def Buffer is list of [1,3,4,2,-1,0,-4,10];
  
  def XX is parseColon((A:Average(3h)) < (B:(3*StdDev(3h))))
  
  prc main() do
  {
    logMsg(info,"$XX");
    
    logMsg(info,"A is $(XX.A)");
    logMsg(info,"B is $(XX.B)");
    logMsg(info,"result is $(XX.result)");
    assert XX.result=true;
  }
}
