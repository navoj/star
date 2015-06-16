/**
 * priority queues 
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
  type queue of elem is eQ or queueNd(elem,cons of queue of elem);
  
  def emptyQ is eQ;
  fun isEmptyQ(eQ) is true
   |  isEmptyQ(_) default is false;
  
  fun insertQ(el,Q) is mergeQ(queueNd(el,nil));
  
  fun mergeQ(h,eQ) is h
   |  mergeQ(eQ,h) is h
   |  mergeQ(h1 matching queueNd(x,hs1), h2 matching queueNd(y,hs2)) is
  	    C.le(x,y) ? queueNd(x, cons(h2,hs1)) : queueNd(y, cons(h1,hs2));
  	
  private 
  fun mergePairs(nil) is eQ
   |  mergePairs(cons(H,nil)) is H
   |  mergePairs(cons(h1,cons(h2,hs))) is mergeQ(mergeQ(h1,h2),mergePairs(hs));
  
  fun findQMin(eQ) is raise "empty"
   |  findQMin(queueNd(x,_)) is x;
  
  fun deleteQMin(eQ) is raise "empty"
   |  deleteQMin(queueNd(_,hs)) is mergePairs(hs);
}



