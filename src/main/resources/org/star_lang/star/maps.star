/**
 * implement the various contracts for hash maps
 * Copyright (C) 2013 Starview Inc
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

private import base;
private import sequences;
private import strings;
private import iterable;
private import updateable;
private import casting;
private import cons;
private import folding;
private import option;

type hash of (%k,%v) is alias of map of (%k,%v);


-- macro for old hash notation
#hash { ?X } ==> map of { X };
#hash{} ==> map of {};
#hash of {} ==> map of {};
#hash of {?X} ==> map of {X};


implementation equality over (map of (%k,%v) where equality over %k 'n equality over %v) is {
  X=Y is hash_equal(X,Y);
} using {
  hash_equal(X,Y) is __hash_equal(X,Y,(=));
}

implementation sizeable over map of (%s,%t) is {
  isEmpty(M) is __hash_empty(M);
  size(R) is integer(__hash_size(R));
}

implementation pPrint over map of (%k,%v) where pPrint over %k 'n pPrint over %v is {
  ppDisp(M) is ppSequence(0,cons(ppStr("map of {"),
	cons(ppSequence(2,dispEntries(M)),cons(ppStr("}"),nil))))
} using {
  dispEntries(M) is cons of { all ppSequence(0,cons(ppDisp(K),cons(ppStr("->"),cons(ppDisp(V),cons(ppStr(";"),cons(ppNl,nil)))))) where
      K->V in M}
}

implementation indexable over map of (%k,%v) determines (%k,%v) is {
  _index(M,K) is __hashGet(M,K)
  _set_indexed(M,K,V) is __hashUpdate(M,K,V);
  _delete_indexed(M,K) is __hashDelete(M,K);
}

implementation updateable over map of (%k,%v) determines ((%k,%v)) is {
  _extend(L,(K,V)) is __hashUpdate(L,K,V);
  _merge(L,R) is __hash_merge(L,R);
  _delete(R,P) is __delete_from_hash(R,P);
  _update(R,M,F) is __update_into_hash(R,M,F);
};

implementation sequence over map of (%k,%v) determines ((%k,%v)) is {
  _cons((K,V),H) is __hashUpdate(H,K,V);
  _apnd(H,(K,V)) is __hashUpdate(H,K,V);
  _nil() is __hashCreate();
  _empty() from X where __hash_empty(X);
  _pair((raise "not implemented"),(raise "not implemented")) from X;
  _back((raise "not implemented"),(raise "not implemented")) from X;
}

implementation iterable over map of (%k,%v) determines %v is {
  _iterate(M,F,S) is __hash_iterate(M,F,S);
}

implementation indexed_iterable over map of (%k,%v) determines (%k,%v) is {
  _ixiterate(M,F,S) is __hash_ix_iterate(M,F,S);
}

implementation foldable over map of (%k,%v) determines %v is {
  leftFold(F,I,A) is __hash_left_fold(A,F,I);
  rightFold(F,I,A) is __hash_right_fold(A,F,I);
  leftFold1(F,A) is __hash_left_fold1(A,F);
  rightFold1(F,A) is __hash_right_fold1(A,F);
}

implementation concatenate over map of (%k,%v) is {
  _concat(M1,M2) is __hash_merge(M1,M2);
}

implementation grouping over cons determines (map, %k,%v) is {
  L group by C is groupByCons(L,C,map of {})
} using {
  groupByCons(nil,_,M) is M
  groupByCons(cons(H,T),C,M) is valof{
    Key is C(H);
    if M[Key] matches group then
      valis groupByCons(T,C,M[with Key->cons(H,group)])
    else
      valis groupByCons(T,C,M[with Key->cons(H,nil)])
  }
}
