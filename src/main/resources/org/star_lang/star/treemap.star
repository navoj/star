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
private import bitstring;
private import arraymap;
private import sequences;
private import strings;
private import casting;
private import cons;
private import arrays;
private import updateable;
private import arithmetic;
private import folding;
private import iterable;

type treemap of (%k,%v) is trNode{
  maskLen has type integer_;
  mask has type integer_;
  left has type treemap of (%k,%v);
  right has type treemap of (%k,%v);
} or trEmpty or trLeaf(integer_,seqmap of (%k,%v));

private
fun treeOk(T) where validTree(T,0_) is true
 |  treeOk(T) default is valof{
      logMsg(info,"tree #(__display(T)) is not valid");
      valis false
    }

fun validTree(trEmpty,_) is true
 |  validTree(trLeaf(_,_),_) is true
 |  validTree(trNode{maskLen=Ln;left=L;right=R},D) where __integer_ge(Ln,D) and __integer_lt(Ln,ThirtyTwo) is
      validTree(L,Ln) and validTree(R,Ln)
 |  validTree(T,Ln) default is false;

implementation pPrint over treemap of (%k,%v) where pPrint over %k and pPrint over %v is {
  fun ppDisp(H) is ppSequence(2,cons of {ppStr("treemap of {"); dispHash(H); ppStr("}")});
} using {
  fun dispHash(trNode{left=L; right=R}) is ppSequence(0,cons of {dispHash(L); dispHash(R)})
   |  dispHash(trEmpty) is ppStr("")
   |  dispHash(trLeaf(_,Els)) is ppSeqMap(Els);
}

implementation indexable over treemap of (%k,%v) determines (%k,%v) where equality over %k is {
  fun _index(M,K) is look(K,M);
  fun _set_indexed(M,K,V) is insrt(K,V,M);
  fun _delete_indexed(M,K) is remve(__hashCode(K),K,M);
}

private
fun look(K,T) is valof{
  def H is __hashCode(K);
  var Tr := T;
  while true do{
    case Tr in {
      trLeaf(H1,Els) where __integer_eq(H1,H) do valis _index(Els,K);
      trNode{maskLen=Ln;mask=M;left=L;right=R} do{
        CM is commonMask(H,Ln);

	    if __integer_eq(CM,M) then{
	      if nthBit(H,Ln) then
	        Tr := R
	      else
	        Tr := L;
	    }
	    else
	      valis none
      };
      _ default do valis none
    }
  }
};

private
fun insrt(K,V,S) is mergeTree(S,trLeaf(__hashCode(K), seqmap(list of {(K,V)})));

private def mOne is -1_;

private def MSB is __integer_bit_neg(__integer_bit_shr(mOne,1_));

private def ThirtyTwo is 32_;

commonMaskLen has type (integer_,integer_)=>integer_;
private
fun commonMaskLen(H1,H2) is valof{
  var C := ThirtyTwo;
  var HH1 := H1;
  var HH2 := H2;
  
  while __integer_ne(HH1,HH2) and __integer_gt(C,0_) do {
    HH1 := __integer_bit_shr(HH1,1_);
    HH2 := __integer_bit_shr(HH2,1_);
    C := __integer_minus(C,1_);
  };
  
  valis C 
};

private
fun commonMask(M1,ML) is valof{
  if __integer_eq(ML,0_) then
    valis 0_
  else{
    CML is __integer_minus(ThirtyTwo,ML);
    valis __integer_bit_and(__integer_bit_shl(__integer_bit_shr(mOne,CML),CML),M1);
  }
} 

private
fun truncatedMask(M,L) is valof{
  def Ln is __integer_minus(ThirtyTwo,L);
  valis __integer_bit_shl(__integer_bit_shr(mOne,Ln),Ln);
}

private
fun hex(I) is string(__integer_hex(I));

private
fun mask(trEmpty) is 0_
 |  mask(trLeaf(M,_)) is M
 |  mask(trNode{mask=M}) is M;

private 
fun maskLen(trEmpty) is ThirtyTwo
 |  maskLen(trLeaf(H,_)) is ThirtyTwo
 |  maskLen(trNode{maskLen=ML}) is ML;

private nthBit(M,L) is valof{
  def Nth is __integer_bit_shr(MSB,L);
  valis __integer_eq(__integer_bit_and(M,Nth),Nth);
}

private
fun mergeTree(trEmpty,T) is T
 |  mergeTree(T,trEmpty) is T
 |  mergeTree(T1,T2) is mergeNodes(T1,T2);

private
fun mergeLeafs(trLeaf(H,L1),trLeaf(H,L2)) is trLeaf(H,_merge(L1,L2))
 |  mergeLeafs(T1 matching trLeaf(H1,L1),T2 matching trLeaf(H2,L2)) is valof{
      def CML is commonMaskLen(H1,H2);
      def CM is  commonMask(H1,CML);
  
      if nthBit(H1,CML) then
        valis trNode{mask=CM;maskLen=CML;left=T2;right=T1}
      else
        valis rNode{mask=CM;maskLen=CML;left=T1;right=T2}
    };

mergeNodes has type (treemap of (%k,%v), treemap of (%k,%v)) => treemap of (%k,%v) where equality over %k;
fun mergeNodes(T1 matching trLeaf(_,_),T2 matching trLeaf(_,_)) is mergeLeafs(T1,T2)
 |  mergeNodes(T1 matching trNode{maskLen=Ln1;mask=M1;left=L1;right=R1}, T2 matching trLeaf(M2,_)) is valof{            
      def CML is rawMin(commonMaskLen(M1,M2),Ln1);
      def CM is commonMask(M1,CML);
  
  -- logMsg(info,"merge #(__display(T2)) into #(__display(T1))");
  -- logMsg(info,"CML=#(integer(CML)), CM=#(hex(CM))");
  
      if __integer_lt(CML,Ln1) then{
        if nthBit(M2,CML) then
          valis trNode{mask=CM;maskLen=CML;left=T1;right=T2}
        else
          valis trNode{mask=CM;maskLen=CML;left=T2;right=T1}
      } else if nthBit(M2,CML) then
        valis trNode{mask=CM;maskLen=CML;left=L1;right=mergeNodes(R1,T2)}
      else
        valis trNode{mask=CM;maskLen=CML;left=mergeNodes(L1,T2);right=R1}
    }
 |  mergeNodes(T1 matching trLeaf(M1,_), T2 matching trNode{maskLen=Ln1;mask=M2;left=L1;right=R1}) is valof{
      def CML is rawMin(commonMaskLen(M1,M2),Ln1);
      def CM is commonMask(M1,CML);
  
      if __integer_lt(CML,Ln1) then{
        if nthBit(M2,CML) then
          valis trNode{mask=CM;maskLen=CML;left=T1;right=T2}
        else
          valis trNode{mask=CM;maskLen=CML;left=T2;right=T1}
      } else if nthBit(M2,CML) then
        valis trNode{mask=CM;maskLen=CML;left=L1;right=mergeNodes(T1,R1)}
      else
        valis trNode{mask=CM;maskLen=CML;left=mergeNodes(T1,L1);right=R1}
    }
 |  mergeNodes(T1 matching trNode{maskLen=Ln1;mask=M1;left=L1;right=R1},T2 matching trNode{maskLen=Ln2;mask=M2;left=L2;right=R2}) is valof{
      def CML is rawMin(rawMin(commonMaskLen(M1,M2),Ln1),Ln2);
      def CM is commonMask(M1,CML);
  
      if __integer_lt(CML,Ln1) then {
        -- split the left tree
        if nthBit(M1,CML) then {
          valis trNode{mask=CM;maskLen=CML;left=L1;right=mergeNodes(R1,T2)}
        } else 
          valis trNode{mask=CM;maskLen=CML;left=mergeNodes(L1,T2);right=R1}
      } else if __integer_lt(CML,Ln2) then {        
    -- split the right tree
        if nthBit(M2,CML) then
          valis trNode{mask=CM;maskLen=CML;left=L2;right=mergeNodes(R2,T1)}
        else
          valis trNode{mask=CM;maskLen=CML;left=mergeNodes(L2,T1);right=R2}
      } else
        valis trNode{mask=CM;maskLen=CML;left=mergeNodes(L1,L2);right=mergeNodes(R1,R2)} 
    }

private
fun remve(_,_,trEmpty) is trEmpty
 |  remve(H,K,L matching trLeaf(H1,Els)) is __integer_eq(H,H1) ? removeLeaf(H1,_delete_indexed(Els,K)):L
 |  remve(H,K,T matching trNode{mask=M;maskLen=Ln;left=L;right=R}) is valof{
      def CM is commonMask(H,Ln);
  
      if __integer_eq(CM,M) then{
        if nthBit(H,Ln) then{
          def NR is remve(H,K,R);
          case NR in {
	        trEmpty do valis L;
	        _ default do valis trNode{mask=M;maskLen=Ln;left=L;right=NR}
          }
        } else {
          def NL is remve(H,K,L);
          case NL in {
	        trEmpty do valis R;
	        _ default do valis trNode{mask=M;maskLen=Ln;left=NL;right=R}
          }
        }
      }
      else
        valis T; -- not present
    }

private fun removeElement(K,S) is remve(__hashCode(K),K,S);

private
fun removeLeaf(_,seqmap(_empty())) is trEmpty
 |  removeLeaf(H,L) is trLeaf(H,L);

private
fun sze(trEmpty) is 0
 |  sze(trLeaf(_,Els)) is size(Els)
 |  sze(trNode{left=L;right=R}) is sze(L)+sze(R);

fun tree_depth(trEmpty) is 0
 |  tree_depth(trLeaf(_,_)) is 1
 |  tree_depth(trNode{left=L;right=R}) is max(tree_depth(L),tree_depth(R))+1;

implementation sequence over treemap of (%k,%v) determines ((%k,%v)) where equality over %k is {
  fun _cons((K,V),M) is insrt(K,V,M);
  fun _apnd(M,(K,V)) is insrt(K,V,M);
  fun _nil() is trEmpty;
  ptn _empty() from trEmpty;
  ptn _pair((raise "not implemented a"),(raise "not implemented b")) from X; 
  ptn _back((raise "not implemented a"),(raise "not implemented b")) from X; 
}

implementation sizeable over treemap of (%k,%v) is {
  fun isEmpty(trEmpty) is true
   |  isEmpty(_) default is false;
  
  fun size(T) is sze(T);
}

private
fun iter(trLeaf(H,Els),F,S) is listMapIter(Els,F,S)
 |  iter(trEmpty,F,S) is S
 |  iter(trNode{left=L;right=R},F,S) is case iter(L,F,S) in {
      ContinueWith(X) is iter(R,F,ContinueWith(X));
      NoneFound is iter(R,F,NoneFound);
      NoMore(X) is NoMore(X);
    }

implementation iterable over treemap of (%k,%v) determines %v is {
  fun _iterate(M,F,S) is iter(M,F,S);
}

implementation indexed_iterable over treemap of (%k,%v) determines (%k,%v) is {
  fun _ixiterate(M,F,S) is ixIter(M,F,S);
}

private
fun ixIter(trLeaf(H,Els),F,S) is listmapIxIter(Els,F,S)
 |  ixIter(trEmpty,F,S) is S
 |  ixIter(trNode{left=L;right=R},F,S) is case ixIter(L,F,S) in {
      ContinueWith(X) is ixIter(R,F,ContinueWith(X));
      NoneFound is ixIter(R,F,NoneFound);
      NoMore(X) is NoMore(X);
    }

implementation updateable over treemap of (%k,%v) determines ((%k,%v)) where equality over %k is {
  fun _extend(TM,(K,V)) is insrt(K,V,TM);
  fun _merge(TM, S) is mergeTree(TM,S);
  fun _delete(TM, P) is _checkIterState(ixIter(TM,iterDelete(P),ContinueWith(TM)),razer);
  fun _update(TM, M, U) is let{
    fun collectUpdates(trEmpty,Ups) is (trEmpty,Ups)
     |  collectUpdates(trLeaf(H,seqmap(Els)),Ups) is newLeaf(H,collectEls(Els,Ups,list of []))
     |  collectUpdates(trNode{left=L;right=R},Ups) is valof{
          def (LL,Lu) is collectUpdates(L,Ups);
          def (RR,Ru) is collectUpdates(R,Lu);
          valis (mergeTree(LL,RR),Ru);
        };

    fun collectEls(_empty(),Ups,Rem) is (Ups,Rem)
     |  collectEls(_pair(E,Rest),Ups,Rem) where E matches M() is collectEls(Rest,cons of [U(E),..Ups],Rem)
     |  collectEls(_pair(E,Rest),Ups,Rem) is collectEls(Rest,Ups,list of [E,..Rem]);
    
    fun newLeaf(_,(Ups,_empty())) is (trEmpty,Ups)
     |  newLeaf(H,(Ups,L)) is (trLeaf(H,seqmap(L)),Ups);
  } in applyUpdates @ collectUpdates(TM,cons of {});
  
  private
  fun applyUpdates(Tr,_empty()) is Tr
   |  applyUpdates(Tr,_pair((K,V),Rest)) is applyUpdates(insrt(K,V,Tr),Rest);
  
  private fun iterInsert((K,V),ContinueWith(M)) is ContinueWith(insrt(K,V,M));
  
  private fun iterDelete(P) is let{
    fun deleter(K,V,ContinueWith(M)) where (K,V) matches P() is ContinueWith(removeElement(K,M))
     |  deleter(_,_,St) default is St;
  } in deleter;
  
  private fun razer() is raise "problem";
} 

implementation concatenate over treemap of (%k,%v) where equality over %k is {
  (++) = _merge;
};

implementation foldable over treemap of (%k,%v) determines %v is {
  leftFold = lftFold;
  rightFold = rgtFold;
} using {
  fun lftFold(_,I,trEmpty) is I
   |  lftFold(F,I,trLeaf(_,Lfs)) is leftFold(F,I,Lfs)
   |  lftFold(F,I,trNode{left=L;right=R}) is lftFold(F,lftFold(F,I,L),R);
  
  fun rgtFold(_,I,trEmpty) is I
   |  rgtFold(F,I,trLeaf(_,Lfs)) is rightFold(F,I,Lfs)
   |  rgtFold(F,I,trNode{left=L;right=R}) is rightFold(F,rightFold(F,I,R),L);
}

private 
fun rawMin(X,Y) where __integer_le(X,Y) is X
 |  rawMin(X,Y) is Y;
