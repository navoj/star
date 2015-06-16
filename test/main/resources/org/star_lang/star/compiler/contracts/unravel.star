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
unravel is package{

  unravel has type (%coll)=>(%left,%right) where
    sequence over %coll determines ((%l,%r)) and
    sequence over %left determines %l and
    sequence over %right determines %r;

  fun unravel(LL) is let{
    fun unravl(sequence of {},L,R) is (L,R)
     |  unravl(sequence of {(El,Er);..M},L,R) is unravl(M,sequence of {L..;El},sequence of {R..;Er})
  } in unravl(LL,sequence of {},sequence of {});
    
  prc main() do {
    def Lin is list of [(1,"alpha"), (2,"beta"), (3,"gamma")];
    
    def (Lf,Rg) is unravel(Lin);
    
    assert Lf = list of [1,2,3];
    assert Rg = list of ["alpha","beta","gamma"];
  }
} 