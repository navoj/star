/**
 * implement arrays and their contracts
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
private import folding;
private import casting;
private import maps;

type list of %a is alias of array of %a;

implementation equality over array of %t where equality over %t is {
  X = Y is arrayEqual(X,Y);
} using {
  arrayEqual(A1,A2) is __array_equal(A1,A2,(=));
}
 
implementation sizeable over array of %t is {
  isEmpty(L) is __array_empty(L);
  size(L) is integer(__array_size(L));
}

implementation pPrint over array of %t where pPrint over %t is {
  ppDisp(L) is ppArray(L);
} using {
  ppArray(L) is ppSequence(0,cons(ppStr("array of {"),cons(ppSequence(2,dispList(L,ppStr(""))),cons(ppStr("}"),nil))));
  inter is ppStr(";");
  dispList(array of{},_) is nil;
  dispList(array of{H;..T},Sep) is cons(Sep,cons(ppDisp(H),dispList(T,inter)));
}

implementation sequence over array of %e determines %e is {
  _cons(H,T) is __array_cons(H,T);
  _apnd(T,H) is __array_append(T,H);
  _empty() from __array_empty_match();
  _pair(H,T) from __array_head_match(H,T);
  _back(F,E) from __array_tail_match(F,E);
  _nil() is __array_nil();
}

implementation concatenate over array of %t is {
  _concat(L,R) is __array_concatenate(L,R);
}

implementation indexable over array of %e determines (integer,%e) is {
  _index(L,integer(Ix)) is __array_element(L,Ix);
  _set_indexed(L,integer(Ix),E) is __array_set_element(L,Ix,E);
  _delete_indexed(L,integer(Ix)) is __array_delete_element(L,Ix);
}

implementation sliceable over array of %e is {
  _slice(L,integer(Fr),integer(To)) is __array_slice(L,Fr,To);
  _tail(L,integer(Fr)) is __array_slice(L,Fr,__array_size(L));
  _splice(L,integer(Fr),integer(To),R) is __array_splice(L,Fr,To,R);
}

implementation iterable over array of %e determines %e is {
  _iterate(R,F,S) is __array_iterate(R,F,S);
}

implementation indexed_iterable over array of %e determines (integer,%e) is {
  _ixiterate(M,F,S) is __array_ix_iterate(M,F,S);
}

implementation updateable over array of %t determines %t is {
  _extend(L,E) is __array_append(L,E);
  _merge(L,R) is __array_concatenate(L,R);
  _delete(R,P) is __delete_from_array(R,P);
  _update(R,M,F) is __update_into_array(R,M,F);
};

implementation sorting over array of %t determines %t is {
  sort(L,C) is __array_sort(L,C);
}

implementation reversible over array of %t is {
  reverse(A) is __array_reverse(A);
}

implementation sets over array of %e where equality over %e is {
  L union R is __array_union(L,R,(=));
  L intersect R is __array_intersect(L,R,(=));
  L complement R is __array_complement(L,R,(=));
}
 
implementation coercion over (array of %t,relation of %t) is {
  coerce(A) is __array_relation(A);
}

implementation coercion over (relation of %t,array of %t) is {
  coerce(A) is __relation_array(A);
}

implementation mappable over array is {
  _map(A,F) is __array_map(A,F);
}

implementation filterable over array of %t determines %t is {
  filter(P,A) is __array_filter(A,P)
}

implementation foldable over array of %e determines %e is {
  leftFold(F,I,A) is __array_left_fold(A,F,I);
  rightFold(F,I,A) is __array_right_fold(A,F,I);
}

implementation grouping over array determines (map, %k,%v) is {
  R group by C is groupBy(R,C)
} using {
  groupBy(Rel,CF) is let{
    f(M,El) is valof{
      Key is CF(El);
      if M[Key] matches group then
        valis M[with Key->array of {group..;El}]
      else
        valis M[with Key->array of {El}]
    }
  } in leftFold(f,map of {},Rel)
}