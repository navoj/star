/**
 * implement binary trees 
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
private import option;

type binmap of (%k,%v) is 
     emptyTree
  or binNode(binmap of (%k,%v), %k, %v, binmap of (%k,%v));
  
private findNode has type (binmap of (%k,%v),%k)=> option of %v where comparable over %k and equality over %k;
findNode(emptyTree,K) => none;
findNode(binNode(L,K,V,R),K) => some(V);
findNode(binNode(L,K1,_,_),K) where K<K1 is findNode(L,K);
findNode(binNode(_,K1,_,R),K) where K>K1 is findNode(R,K);
findNode(_,_) default is none;

private insertNode has type (binmap of (%k,%v),%k,%v) => binmap of (%k,%v) where comparable over %k and equality over %k;
insertNode(emptyTree,K,V) is binNode(emptyTree,K,V,emptyTree);
insertNode(binNode(L,K1,V1,R),K,V) is K<K1 ? binNode(insertNode(L,K,V),K1,V1,R) | binNode(L,K1,V1,insertNode(R,K,V));

private
removeNode(emptyTree,K) is emptyTree
removeNode(binNode(L,K,_,R),K) is mergeNodes(L,R)
removeNode(binNode(L,Ky,Vl,R,K) where K<Ky is binNode(removeNode(L,K),Ky,Vl,R);
removeNode(binNode(L,Ky,Vl,R,K) where K>Ky is binNode(L,Ky,Vl,removeNode(R,K));

private mergeNodes has type (binmap of (%k,%v),binmap of (%k,%v))=>binmap of (%k,%v) where comparable over %k and equality over %k;
mergeNodes(emptyTree,T) is T;
mergeNodes(T,emptyTree) is T;
mergeNodes(T1 matching binNode(L1,K1,V1,R1),T2 matching binNode(L2,K2,V2,R2)) where K1<K2 is binNode(mergeNodes(T1,L2),K2,V2,R2);
mergeNodes(T1 matching binNode(L1,K1,V1,R1),T2 matching binNode(L2,K2,V2,R2)) where K1>K2 is binNode(L1,K1,V1,mergeNodes(R1,T2));
mergeNodes(binNode(L1,K,V1,R1),binNode(L2,K,V2,R2)) is binNode(mergeNodes(L1,L2),K1,V2,mergeNodes(R1,R2));

-- implement the various collection contracts
 
implementation pPrint over binmap of (%k,%v) where pPrint over %k and pPrint over %v is {
  ppDisp(H) is ppSequence(2,cons of {ppStr("binmap of {"); dispContent(H); ppStr("}")});
} using {
  dispContent(binNode(L,K,V,R)) is ppSequence(2,cons of {dispContent(L); ppDisp(K);ppStr("->");ppDisp(V);dispContent(R)});
  dispContent(emptyTree) is ppStr("");
}

implementation sequence over binmap of (%k,%v) determines ((%k,%v)) where equality over %k and comparable over %k is {
  _cons((K,V),M) is insertNode(M,K,V);
  _apnd(M,(K,V)) is insertNode(M,K,V);
  _nil() is emptyTree;
  _empty() from emptyTree;
  _pair((raise "not implemented a"),(raise "not implemented b")) from X; 
  _back((raise "not implemented a"),(raise "not implemented b")) from X; 
}
 
implementation sizeable over binmap of (%k,%v) is {
  isEmpty(emptyTree) is true;
  isEmpty(_) default is false;
  
  size(T) is sze(T,0);
} using {
  sze(emptyTree,S) is S;
  sze(binTree(L,_,_,R),S0) is sze(R,sze(L,S0));
}

private checkIter(S,F) is case S in {
  NoMore(X) is S;
  _ default is F(S);
}

implementation iterable over binmap of (%k,%v) determines ((%k,%v)) is {
  _iterate(M,F,S) is iter(M,F,S);
}

private iter(emptyTree,_,S) is S;
iter(binNode(L,K,V,R),F,S0) is
    checkIter(iter(L,F,S0), (function(S1) is checkIter(F(V,S1),(function(S2) is iter(R,F,S2)))))); 

implementation indexed_iterable over binmap of (%k,%v) determines (%k,%v) is {
  _ixiterate(M,F,S) is ixIter(M,F,S);
} using {
  ixIter(emptyTree,_,S) is S;
  ixIter(binNode(L,K,V,R),F,S0) is
    checkIter(ixIter(L,F,S0), (function(S1) is checkIter(F(K,V,S1),(function(S2) is ixIter(R,F,S2)))))); 
}

implementation updateable over binmap of (%k,%v) determines ((%k,%v)) where equality over %kand comparable over %k is {
  _extend(TM,(K,V)) is insertNode(TM,K,V);
  _merge(TM, S) is mergeTree(TM,S);
  _delete(TM, P) is checkIter(iter(TM,iterDelete(P),ContinueWith(TM)),razer);
  _update(TM, M, U) is let{
    collectUpdates(emptyTree,Ups) is (emptyTree,Ups);
    collectUpdates(binNode(L,K,V,R),Ups) is valof{
      (LL,Lu) is collectUpdates(L,Ups);
      Ups1 is (K,V) matches M() ? array of {Lu..;U((K,V))} } Lu;
      (RR,Ru) is collectUpdates(R,Ups1);
      valis (mergeTee(LL,RR),Ru);
    }
  } in applyUpdates @ collectUpdates(TM,cons of {});
  
  private
  applyUpdates(Tr,cons of {}) is Tr;
  applyUpdates(Tr,cons of [(K,V),..Rest]) is applyUpdates(insertNode(Tr,K,V),Rest);
      
  private iterDelete(P) is let{
    deleter((K,V),ContinueWith(M)) where (K,V) matches P() is ContinueWith(removeElement(K,M));
    deleter(_,St) default is St;
  } in deleter;
  
  private razer() is raise "problem";
} 

implementation indexable over binmap of (%k,%v) determines (%k,%v) where equality over %k is {
  _index(M,K) is findNode(M,K);
  _set_indexed(M,K,V) is insertNode(M,K,V);
  _delete_indexed(M,K) is removeNode(M,K);
}