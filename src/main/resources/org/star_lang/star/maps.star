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
private import cons;
private import folding;
private import option;

public implementation equality over (dictionary of (%k,%v) where equality over %k and equality over %v) is {
  fun X=Y is hash_equal(X,Y);
  fun hashCode(D) is integer(__hashCode(D))
} using {
  fun hash_equal(X,Y) is __hash_equal(X,Y,(=));
}

public implementation sizeable over dictionary of (%s,%t) is {
  fun isEmpty(M) is __hash_empty(M);
  fun size(R) is integer(__hash_size(R));
}

public implementation pPrint over dictionary of (%k,%v) where pPrint over %k and pPrint over %v is {
  fun ppDisp(M) is ppSequence(0,cons(ppStr("dictionary of ["),
	                    cons(ppSequence(2,dispEntries(M)),cons(ppStr("]"),nil))))
} using {
  fun dispEntries(M) is interleave(cons of { all ppSequence(0,cons(ppDisp(K),cons(ppStr("->"),cons(ppDisp(V),nil)))) where
      K->V in M},ppStr(","))
}

public implementation indexable over dictionary of (%k,%v) determines (%k,%v) is {
  fun _index(M,K) is __hashGet(M,K)
  fun _set_indexed(M,K,V) is __hashUpdate(M,K,V);
  fun _delete_indexed(M,K) is __hashDelete(M,K);
}

public implementation updateable over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun _extend(L,(K,V)) is __hashUpdate(L,K,V);
  fun _merge(L,R) is __hash_merge(L,R);
  fun _delete(R,P) is __delete_from_hash(R,P);
  fun _update(R,M,F) is __update_into_hash(R,M,F);
};

public implementation sequence over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun _cons((K,V),H) is __hashUpdate(H,K,V);
  fun _apnd(H,(K,V)) is __hashUpdate(H,K,V);
  fun _nil() is __hashCreate();
  ptn _empty() from X where __hash_empty(X);

  ptn _pair(__hash_pick(X),__hash_remaining(X)) from X where not __hash_empty(X)
  ptn _back(__hash_remaining(X),__hash_pick(X)) from X where not __hash_empty(X)
}

public implementation iterable over dictionary of (%k,%v) determines %v is {
  fun _iterate(M,F,S) is __hash_iterate(M,F,S);
}

public implementation indexed_iterable over dictionary of (%k,%v) determines (%k,%v) is {
  fun _ixiterate(M,F,S) is __hash_ix_iterate(M,F,S);
}

public implementation foldable over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun leftFold(F,I,A) is __hash_left_fold(A,F,I);
  fun rightFold(F,I,A) is __hash_right_fold(A,F,I);
  fun leftFold1(F,A) is __hash_left_fold1(A,F);
  fun rightFold1(F,A) is __hash_right_fold1(A,F);
}

public implementation concatenate over dictionary of (%k,%v) is {
  fun M1++M2 is __hash_merge(M1,M2);
}

public implementation grouping over cons determines (dictionary, %k,%v) is {
  fun L group by C is groupByCons(L,C,dictionary of [])
} using {
  fun groupByCons(nil,_,M) is M
   |  groupByCons(cons(H,T),C,M) is valof{
        def Key is C(H);
        if M[Key] has value group then
          valis groupByCons(T,C,M[with Key->cons(H,group)])
        else
          valis groupByCons(T,C,M[with Key->cons(H,nil)])
      }
}
