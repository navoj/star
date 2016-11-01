/**
 * splay queue 
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */


private import sequences;
private import arithmetic;
private import cons;
private import signatures;

public priorityFun has type for all elem such that (comparison of elem)=>priorityQ of elem;
fun priorityFun(C) is priorityQ{
  type splay of elem is eQ or Nd(splay of elem, elem, splay of elem);
  
  def emptyQ is eQ;
  fun isEmptyQ(eQ) is true
   |  isEmptyQ(_) default is false;
  
  private
  fun partition(pivot,eQ) is (eQ,eQ)
   |  partition(pivot,t matching Nd(a,x,b)) is
  	    C.le(x,pivot) ?
  	      switch b in {
  	        case eQ is (t,eQ);
  	        case Nd(b1,y,b2) is valof{
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