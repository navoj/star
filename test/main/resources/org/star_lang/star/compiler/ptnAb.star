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
ptnAb is package{
  -- test the pattern abstraction stuff
  
  positive has type (integer) <= integer;
  ptn positive(I) from (I where I>0);
  
  filter has type (list of %t, (%s)<=%t) => list of %s;
  fun filter(L,P) is let{
    fun flt(list of []) is list of []
     |  flt(list of [P(I),..More]) is list of [I,..flt(More)]
     |  flt(list of [_,..More]) default is flt(More)
  } in flt(L);
  
  ptn lee(X) from (("lee",X));
  
  prc main() do
  {
    def LL is list of [1,-2,34,-1,-2,10,0,-1];
    
    logMsg(info,"filter of $LL is $(filter(LL,positive))");
    
    assert filter(LL,positive)=list of [1,34,10];
    
    def MM is list of [("lee",1), ("lee",2), ("lea",3), ("bar",01), ("lee",3)];
    logMsg(info,"filter of $MM is $(filter(MM,lee))");
    
    assert filter(MM,lee)=list of [1,2,3];
  }
}