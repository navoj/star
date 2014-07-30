/**
 * implement simple relations and their contracts
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
private import folding;
private import maps;

-- macro for old relation notation
#relation { ?X } ==> relation of { X };
#relation{} ==> relation of {};

implementation equality over relation of %t where equality over %t is {
  X = Y is equal(X,Y);
} using {
  equal(X,Y) is __relation_eq(X,Y,(=));
}

implementation sizeable over relation of %t is {
  isEmpty = relationEmpty;
  size = relationSize;
} using {
  relationSize(R) is integer(__relation_size(R));
  relationEmpty(R) is __relation_empty(R);
}

implementation pPrint over relation of %t where pPrint over %t is {
  ppDisp(L) is ppElements(L);
} using {
  ppElements(L) is ppSequence(0,cons(ppStr("relation of {"),cons(ppSequence(2,dispList(L,ppStr(""))),cons(ppStr("}"),nil))));
  inter is ppStr(";");
  dispList(relation of {},_) is nil;
  dispList(relation of {H;..T},Sep) is cons(Sep,cons(ppDisp(H),dispList(T,inter)));
}

implementation sequence over relation of %e determines %e is {
  _cons(H,T) is __relationCons(H,T);
  _apnd(R,E) is __relationCons(E,R);
  _nil() is __newRelation();
  _empty() from X where isEmpty(X);
  _pair(H,T) from __relation_head_match(H,T);
  _back(F,E) from __relation_tail_match(F,E);
 }
    
implementation concatenate over relation of %t is {
  _concat(L,R) is __merge_relation(L,R);
}

implementation iterable over relation of %e determines %e is {
  _iterate(R,F,S) is __relation_iterate(R,F,S);
}
 
 /*
implementation indexedIterable over relation of %e determines %e is {
  indexedIterate(R,C,K,F,S) is __relation_indexed_iterate(R,C,K,F,S);
};
 */

implementation updateable over relation of %t determines %t is {
  _extend(R,T) is __extend_relation(R,T);
  _merge(R, S) is __merge_relation(R,S);
  _delete(R, P) is __delete_from_relation(R,P);
  _update(R, M, F) is __update_into_relation(R,M,F);
}

implementation sets over relation of %e where equality over %e is {
  L union R  is __relation_union(L,R,(=));
  L intersect R is __relation_intersect(L,R,(=));
  L complement R is __relation_complement(L,R,(=));
}

implementation sliceable over relation of %e determines integer is {
  _slice(L,integer(Fr),integer(To)) is __relation_slice(L,Fr,To);
  _tail(L,integer(Fr)) is __relation_slice(L,Fr,__relation_size(L));
  _splice(L,integer(Fr),integer(To),R) is __relation_splice(L,Fr,To,R);
}
 
implementation mappable over relation is {
  _map(R,F) is __relation_map(R,F);
}

implementation filterable over relation of %t determines %t is {
  filter(P,A) is __relation_filter(A,P)
}
implementation foldable over relation of %e determines %e is {
  leftFold(F,I,IR) is __relation_left_fold(IR,F,I);
  rightFold(F,I,IR) is __relation_right_fold(IR,F,I);
}

implementation sorting over relation of %t determines %t is {
  sort(L,C) is __relation_sort(L,C);
}

implementation grouping over relation determines (map, %k,%v) is {
  R group by C is relationGroup(R,C)
} using {
  relationGroup(Rel,CF) is let{
    f(M,El) is valof{
      Key is CF(El);
      if M[Key] matches group then
        valis M[with Key->relation of {El;..group}]
      else
        valis M[with Key->relation of {El}]
    }
  } in leftFold(f,map of {},Rel)
}