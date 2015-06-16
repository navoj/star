/**
 * splay queue 
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

private import sequences;
private import arithmetic;
private import cons;
private import signatures;

priorityFun has type for all elem such that (comparison of elem)=>priorityQ of elem;
fun priorityFun(C) is priorityQ{
  type splay of elem is eQ or Nd(splay of elem, elem, splay of elem);
  
  def emptyQ is eQ;
  fun isEmptyQ(eQ) is true
   |  isEmptyQ(_) default is false;
  
  private
  fun partition(pivot,eQ) is (eQ,eQ)
   |  partition(pivot,t matching Nd(a,x,b)) is
  	    C.le(x,pivot) ?
  	      case b in {
  	        eQ is (t,eQ);
  	        Nd(b1,y,b2) is valof{
  	          if C.le(y,pivot) then {
  	            def (small, big) is partition(pivot,b2);
  	            valis (Nd(Nd(a,x,b),y,small),big)
  	          } else {
  	            def (small,big) is partition(pivot,b1);
  	            valis (Nd(a,x,small),Nd(big,y,b2));
  	          }
  	      }
  	
  	      
  
  insertQ(el,Q) is mergeQ(queueNd(el,nil));
  
  mergeQ(h,eQ) is h;
  mergeQ(eQ,h) is h;
  mergeQ(h1 matching queueNd(x,hs1), h2 matching queueNd(y,hs2)) is
  	C.le(x,y) ? queueNd(x, cons(h2,hs1)) : queueNd(y, cons(h1,hs2));
  	
  private mergePairs(nil) is eQ;
  mergePairs(cons(H,nil)) is H;
  mergePairs(cons(h1,cons(h2,hs))) is mergeQ(mergeQ(h1,h2),mergePairs(hs));
  
  findQMin(eQ) is raise "empty";
  findQMin(queueNd(x,_)) is x;
  
  deleteQMin(eQ) is raise "empty";
  deleteQMin(queueNd(_,hs)) is mergePairs(hs);
} 