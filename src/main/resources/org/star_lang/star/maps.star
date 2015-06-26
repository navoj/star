/**
 * implement the various contracts for hash maps 
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

private import base;
private import sequences;
private import strings;
private import iterable;
private import updateable;
private import casting;
private import cons;
private import folding;
private import option;

implementation equality over (dictionary of (%k,%v) where equality over %k and equality over %v) is {
  fun X=Y is hash_equal(X,Y);
} using {
  fun hash_equal(X,Y) is __hash_equal(X,Y,(=));
}

implementation sizeable over dictionary of (%s,%t) is {
  fun isEmpty(M) is __hash_empty(M);
  fun size(R) is integer(__hash_size(R));
}

implementation pPrint over dictionary of (%k,%v) where pPrint over %k and pPrint over %v is {
  fun ppDisp(M) is ppSequence(0,cons(ppStr("dictionary of ["),
	cons(ppSequence(2,dispEntries(M)),cons(ppStr("]"),nil))))
} using {
  fun dispEntries(M) is cons of { all ppSequence(0,cons(ppDisp(K),cons(ppStr("->"),cons(ppDisp(V),cons(ppStr(","),cons(ppNl,nil)))))) where
      K->V in M}
}

implementation indexable over dictionary of (%k,%v) determines (%k,%v) is {
  fun _index(M,K) is __hashGet(M,K)
  fun _set_indexed(M,K,V) is __hashUpdate(M,K,V);
  fun _delete_indexed(M,K) is __hashDelete(M,K);
}

implementation updateable over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun _extend(L,(K,V)) is __hashUpdate(L,K,V);
  fun _merge(L,R) is __hash_merge(L,R);
  fun _delete(R,P) is __delete_from_hash(R,P);
  fun _update(R,M,F) is __update_into_hash(R,M,F);
};

implementation sequence over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun _cons((K,V),H) is __hashUpdate(H,K,V);
  fun _apnd(H,(K,V)) is __hashUpdate(H,K,V);
  fun _nil() is __hashCreate();
  ptn _empty() from X where __hash_empty(X);
  ptn _pair((raise "not implemented"),(raise "not implemented")) from X;
  ptn _back((raise "not implemented"),(raise "not implemented")) from X;
}

implementation iterable over dictionary of (%k,%v) determines %v is {
  fun _iterate(M,F,S) is __hash_iterate(M,F,S);
}

implementation indexed_iterable over dictionary of (%k,%v) determines (%k,%v) is {
  fun _ixiterate(M,F,S) is __hash_ix_iterate(M,F,S);
}

implementation foldable over dictionary of (%k,%v) determines ((%k,%v)) is {
  fun leftFold(F,I,A) is __hash_left_fold(A,F,I);
  fun rightFold(F,I,A) is __hash_right_fold(A,F,I);
  fun leftFold1(F,A) is __hash_left_fold1(A,F);
  fun rightFold1(F,A) is __hash_right_fold1(A,F);
}

implementation concatenate over dictionary of (%k,%v) is {
  fun M1++M2 is __hash_merge(M1,M2);
}

implementation grouping over cons determines (dictionary, %k,%v) is {
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
