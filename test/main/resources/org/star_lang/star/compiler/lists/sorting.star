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
-- An example involving defining types and some algorithmic computation

sorting is package{
  msort has type (list of %t ) => list of %t where comparable over %t;
  fun msort(list of [X]) is list of [X]
   |  msort(list of []) is list of []
   |  msort(list of [pivot,..L])  is let{
        fun split(list of [],Lf,R) is (Lf,R)
         |  split(list of [E,..more],Lf,R) where E<pivot is split(more,list of [E,..Lf],R)
         |  split(list of [E,..more],Lf,R) default is split(more,Lf,list of [E,..R])
    
        def (LL,RR) is split(L,list of [],list of [])
      } in msort(LL)++list of [pivot,..msort(RR)]
   
  def L1 is list of [1,5,2,0];
  def L1S is list of [0,1,2,5];
   
  prc main() do {
    logMsg(info,"sort of [1,5,2,0] is $(msort(L1))");
    
    assert msort(L1)=L1S;
  }
}
