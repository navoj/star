/**
 * 
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
private import arrays;
private import cons;
private import strings;
private import sequences;
private import updateable;
private import folding;
private import iterable;

-- a simple implementation of the map interfaces using arrays

type seqmap of (%k,%v) is seqmap(array of ((%k,%v)));

implementation sizeable over seqmap of (%s,%t) is {
  isEmpty(seqmap(array of {})) is true;
  isEmpty(_) default is false;
  size(seqmap(L)) is integer(__array_size(L));
}
  
implementation indexable over seqmap of (%k,%v) determines (%k,%v) where equality over %k is {
  _index(seqmap(L),K) is findEl(L,K);
  _set_indexed(seqmap(L),K,V) is seqmap(replaceEl(L,K,V));
  _delete_indexed(seqmap(L),K) is seqmap(deleteEl(L,K));
}

private
inArray(V) from (array of {(K,V);.._},K);
inArray(V) from (array of {_;..T},K) where (T,K) matches inArray(V); 

private 
findEl(array of {},_) is none;
findEl(array of {E;..L},K) where E matches (K,V) is some(V);
findEl(array of {E;..L},K) is findEl(L,K);
  
private
replaceEl(array of {},K,V) is array of {(K,V)};
replaceEl(array of {E;..L},K,V) where E matches (K,_) is array of {(K,V);..L};
replaceEl(array of {E;..L},K,V) is array of {E;..replaceEl(L,K,V)};

private
deleteEl(array of {},K) is array of {};
deleteEl(array of {E;..L},K) where E matches (K,_) is L;
deleteEl(array of {E;..L},K) is array of {E;..deleteEl(L,K)};

implementation updateable over seqmap of (%k,%v) determines ((%k,%v)) where equality over %k is {
  _extend(seqmap(M),(K,V)) is seqmap(replaceEl(M,K,V));
  _merge(seqmap(M),seqmap(N)) is seqmap(mergeEls(N,M));
  _delete(seqmap(M),Ptn) is seqmap(deleteEls(M,Ptn));
  _update(seqmap(M),Ptn,Up) is seqmap(updateEls(M,Ptn,Up));
}

mergeEls(array of {},M) is M;
mergeEls(array of {(K,V);..L},M) is mergeEls(L,replaceEl(M,K,V));

deleteEls has type (array of %e,()<=%e)=>array of %e;
private deleteEls(M,P) is _delete(M,P);

updateEls has type (array of %e, ()<=%e, (%e)=>%e) => array of %e;
private updateEls(M,P,U) is _update(M,P,U);

implementation iterable over seqmap of (%k,%e) determines %e is {
  _iterate(R,F,S) is arrayMapIter(R,F,S);
}

arrayMapIter(seqmap(R),F,S) is let {
  dropFun((_,E),St) is F(E,St);
} in __array_iterate(R,dropFun,S);

arraymapIxIter(seqmap(R),F,S) is arrayIxIter(R,F,S);

private arrayIxIter(array of {},_,St) is St;
arrayIxIter(_,_,NoMore(X)) is NoMore(X);
arrayIxIter(array of {(K,V);..T},F,St) is arrayIxIter(T,F,F(K,V,St));

implementation pPrint over seqmap of (%k,%v) where pPrint over %k 'n pPrint over %v is {
  ppDisp(Els) is ppSequence(2,cons of {ppStr("arraymap of {"); ppSeqMap(Els); ppStr("}")});
};

ppSeqMap(seqmap(Els)) is ppSequence(0,cons of {ppSequence(2,cons of {ppDisp(K); ppStr("->"); ppDisp(V); ppStr(";"); ppNl})
                                              where (K,V) in Els});  
                                              
implementation foldable over seqmap of (%k,%v) determines %v is {
  leftFold(F,I,M) is lftFold(F,I,M);
  rightFold(F,I,M) is rgtFold(F,I,M);
} using {
  lftFold(F,I,seqmap(L)) is let{
    FF(St,(_,V)) is F(St,V);
  } in leftFold(FF,I,L);
  rgtFold(F,I,seqmap(L)) is let{
    FF((_,V),St) is F(V,St);
  } in rightFold(FF,I,L);
}
    