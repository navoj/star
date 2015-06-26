/**
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
private import cons;
private import strings;
private import sequences;
private import arrays;
private import updateable;
private import folding;
private import iterable;

-- a simple implementation of the dictionary interfaces using lists

type seqmap of (%k,%v) is seqmap(list of ((%k,%v)));

implementation sizeable over seqmap of (%s,%t) is {
  fun isEmpty(seqmap(_empty())) is true
   |  isEmpty(_) default is false
  fun size(seqmap(L)) is integer(__array_size(L));
}
  
implementation indexable over seqmap of (%k,%v) determines (%k,%v) where equality over %k is {
  fun _index(seqmap(L),K) is findEl(L,K);
  fun _set_indexed(seqmap(L),K,V) is seqmap(replaceEl(L,K,V));
  fun _delete_indexed(seqmap(L),K) is seqmap(deleteEl(L,K));
}

private
ptn inArray(V) from (_pair((K,V),_),K)
 |  inArray(V) from (_pair(_,T),K) where (T,K) matches inArray(V)

private 
fun findEl(_empty(),_) is none
 |  findEl(_pair(E,L),K) where E matches (K,V) is some(V)
 |  findEl(_pair(E,L),K) is findEl(L,K)
  
private
fun replaceEl(_empty(),K,V) is list of [(K,V)]
 |  replaceEl(_pair(E,L),K,V) where E matches (K,_) is list of [(K,V),..L]
 |  replaceEl(_pair(E,L),K,V) is list of [E,..replaceEl(L,K,V)]

private
fun deleteEl(_empty(),K) is list of []
 |  deleteEl(_pair(E,L),K) where E matches (K,_) is L
 |  deleteEl(_pair(E,L),K) is list of [E,..deleteEl(L,K)]

implementation updateable over seqmap of (%k,%v) determines ((%k,%v)) where equality over %k is {
  fun _extend(seqmap(M),(K,V)) is seqmap(replaceEl(M,K,V));
  fun _merge(seqmap(M),seqmap(N)) is seqmap(mergeEls(N,M));
  fun _delete(seqmap(M),Ptn) is seqmap(deleteEls(M,Ptn));
  fun _update(seqmap(M),Ptn,Up) is seqmap(updateEls(M,Ptn,Up));
}

fun mergeEls(_empty(),M) is M
 |  mergeEls(_pair((K,V),L),M) is mergeEls(L,replaceEl(M,K,V))

deleteEls has type (list of %e,()<=%e)=>list of %e;
private 
fun deleteEls(M,P) is _delete(M,P);

updateEls has type (list of %e, ()<=%e, (%e)=>%e) => list of %e;
private fun updateEls(M,P,U) is _update(M,P,U);

implementation iterable over seqmap of (%k,%e) determines %e is {
  fun _iterate(R,F,S) is listMapIter(R,F,S);
}

fun listMapIter(seqmap(R),F,S) is let {
  fun dropFun((_,E),St) is F(E,St);
} in __array_iterate(R,dropFun,S);

fun listmapIxIter(seqmap(R),F,S) is listIxIter(R,F,S);

private
fun listIxIter(_empty(),_,St) is St
 |  listIxIter(_,_,NoMore(X)) is NoMore(X)
 |  listIxIter(_pair((K,V),T),F,St) is listIxIter(T,F,F(K,V,St))

implementation pPrint over seqmap of (%k,%v) where pPrint over %k and pPrint over %v is {
  fun ppDisp(Els) is ppSequence(2,cons of [ppStr("listmap of ["), ppSeqMap(Els), ppStr("]")]);
};

fun ppSeqMap(seqmap(Els)) is
  ppSequence(0,cons of {all ppSequence(2,cons of [ppDisp(K), ppStr("->"), ppDisp(V), ppStr(","), ppNl])
                                              where (K,V) in Els});  
                                              
implementation foldable over seqmap of (%k,%v) determines %v is {
  leftFold = lftFold
  rightFold = rgtFold;
} using {
  fun lftFold(F,I,seqmap(L)) is let{
    fun FF(St,(_,V)) is F(St,V);
  } in leftFold(FF,I,L);
  fun rgtFold(F,I,seqmap(L)) is let{
    fun FF((_,V),St) is F(V,St);
  } in rightFold(FF,I,L);
}
    