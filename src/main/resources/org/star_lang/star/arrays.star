/*
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


private import base;
private import sequences;
private import strings;
private import iterable;
private import updateable;
private import folding;
private import casting;
private import maps;
private import sets
private import option;
private import arithmetic;

implementation equality over list of %t where equality over %t is {
  fun X = Y is arrayEqual(X,Y);
  fun hashCode(A) is integer(__hashCode(A))
} using {
  fun arrayEqual(A1,A2) is __array_equal(A1,A2,(=));
}
 
implementation sizeable over list of %t is {
  fun isEmpty(L) is __array_empty(L);
  fun size(L) is integer(__array_size(L));
}

implementation pPrint over list of %t where pPrint over %t is {
  fun ppDisp(L) is sequenceDisplay("list",L);
}

implementation sequence over list of %e determines %e is {
  fun _cons(H,T) is __array_cons(H,T);
  fun _apnd(T,H) is __array_append(T,H);
  ptn _empty() from __array_empty_match();
  ptn _pair(H,T) from __array_head_match(H,T);
  ptn _back(F,E) from __array_tail_match(F,E);
  fun _nil() is __array_nil();
}

implementation concatenate over list of %t is {
  fun L++R is __array_concatenate(L,R);
}

implementation indexable over list of %e determines (integer,%e) is {
  fun _index(L,integer(Ix)) is __array_element(L,Ix);
  fun _set_indexed(L,integer(Ix),E) is __array_set_element(L,Ix,E);
  fun _delete_indexed(L,integer(Ix)) is __array_delete_element(L,Ix);
}

implementation sliceable over list of %e determines integer is {
  fun _slice(L,integer(Fr),integer(To)) is __array_slice(L,Fr,To);
  fun _tail(L,integer(Fr)) is __array_slice(L,Fr,__array_size(L));
  fun _splice(L,integer(Fr),integer(To),R) is __array_splice(L,Fr,To,R);
}

implementation iterable over list of %e determines %e is {
  fun _iterate(R,F,S) is __array_iterate(R,F,S);
}

implementation indexed_iterable over list of %e determines (integer,%e) is {
  fun _ixiterate(M,F,S) is __array_ix_iterate(M,F,S);
}

implementation updateable over list of %t determines %t is {
  fun _extend(L,E) is __array_append(L,E);
  fun _merge(L,R) is __array_concatenate(L,R);
  fun _delete(R,P) is __delete_from_array(R,P);
  fun _update(R,M,F) is __update_into_array(R,M,F);
};

implementation sorting over list of %t determines %t is {
  fun sort(L,C) is __array_sort(L,C);
}

implementation reversible over list of %t is {
  fun reverse(A) is __array_reverse(A);
}

implementation sets over list of %e determines %e where equality over %e is {
  fun L union R is __array_union(L,R,(=));
  fun L intersect R is __array_intersect(L,R,(=));
  fun L complement R is __array_complement(L,R,(=));
  fun add_element(A,E) is __array_append(A,E);
  fun remove_element(A,E) is __delete_from_array(A,(() from EE where E=EE));
  fun contains_element(A,E) is __array_search(A,E,(=))
}
 
implementation mappable over list is {
  fun map(F,A) is __array_map(A,F);
}

implementation filterable over list of %t determines %t is {
  fun filter(P,A) is __array_filter(A,P)
}

implementation foldable over list of %e determines %e is {
  fun leftFold(F,I,A) is __array_left_fold(A,F,I);
  fun rightFold(F,I,A) is __array_right_fold(A,F,I);

  fun leftFold1(F,A) is __array_left_fold1(A,F);
  fun rightFold1(F,A) is __array_right_fold1(A,F);
}

implementation grouping over list determines (dictionary, %k,%v) is {
  fun R group by C is groupBy(R,C)
} using {
  fun groupBy(Rel,CF) is let{
    fun f(M,El) is valof{
      def Key is CF(El);
      if M[Key] has value group then
        valis M[with Key->list of [group..,El]]
      else
        valis M[with Key->list of [El]]
    }
  } in leftFold(f,dictionary of [],Rel)
}