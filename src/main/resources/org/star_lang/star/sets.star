/*
 * Copyright (c) 2015. Francis G. McCabe
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
private import base;
private import sequences;
private import strings;
private import iterable;
private import updateable;
private import casting;
private import folding;
private import cons

#right((union),700);
#right((intersect),600);
#right((complement),500);

contract sets over t determines e is {
  (union) has type (t,t)=>t;
  (intersect) has type (t,t)=>t;
  (complement) has type (t,t)=>t;
  add_element has type (t,e)=>t;
  remove_element has type (t,e)=>t;
  contains_element has type (t,e)=>boolean
}

implementation equality over (set of %v where equality over %v) is {
  fun X=Y is __set_equal(X,Y);
}

implementation sizeable over set of %t is {
  fun isEmpty(M) is __set_empty(M);
  fun size(R) is integer(__set_size(R));
}

implementation pPrint over set of %v where pPrint over %v is {
  fun ppDisp(M) is ppSequence(0,cons(ppStr("set of ["),
 	                    cons(ppSequence(2,interleave(dispEntries(M),ppStr(","))),cons(ppStr("]"),nil))))
 } using {
   fun dispEntries(M) is cons of { all ppDisp(V) where V in M }
 }

implementation updateable over set of %v determines %v is {
  fun _extend(L,V) is __set_insert(L,V);
  fun _merge(L,R) is __set_union(L,R);
  fun _delete(R,P) is __set_filter_out(R,P);
  fun _update(R,M,F) is __set_update(R,M,F);
};

implementation sequence over set of %v determines %v is {
  fun _cons(V,H) is __set_insert(H,V);
  fun _apnd(H,V) is __set_insert(H,V);
  fun _nil() is __set_create();
  ptn _empty() from X where __set_empty(X);

  ptn _pair(__set_pick(X),__set_remaining(X)) from X where not __set_empty(X)
  ptn _back(__set_remaining(X),__set_pick(X)) from X where not __set_empty(X)
}

implementation iterable over set of %v determines %v is {
  fun _iterate(M,F,S) is __set_iterate(M,F,S);
}
implementation mappable over set is {
  fun map(F,S) is __set_map(F,S)
}

implementation foldable over set of %v determines %v is {
  fun leftFold(F,I,A) is __set_left_fold(A,F,I);
  fun rightFold(F,I,A) is __set_right_fold(A,F,I);
  fun leftFold1(F,A) is __set_left_fold1(A,F);
  fun rightFold1(F,A) is __set_right_fold1(A,F);
}

implementation concatenate over set of %v is {
  fun M1++M2 is __set_union(M1,M2);
}

implementation sets over set of %t determines %t is {
  fun X union Y is __set_union(X,Y)
  fun X intersect Y is __set_intersect(X,Y)
  fun X complement Y is __set_difference(X,Y)
  fun add_element(S,E) is __set_insert(S,E)
  fun remove_element(S,E) is __set_delete(S,E)
  fun contains_element(S,E) is __set_contains(S,E)
}