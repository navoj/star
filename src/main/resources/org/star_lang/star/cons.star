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
private import sequences;
private import updateable;
private import arithmetic;
private import strings;
private import folding;
private import iterable;

-- the cons is type built in to the compiler.
-- type cons of %e is nil or cons(%e, cons of %e);

implementation pPrint over cons of %t where pPrint over %t is {
  ppDisp(L) is ppSequence(0,cons(ppStr("cons of {"),cons(ppSequence(2,dispList(L,ppStr(""))),cons(ppStr("}"),nil))));
} using {
  inter is ppStr(";");
  dispList(nil,_) is nil;
  dispList(cons(H,T),Sep) is cons(Sep,cons(ppDisp(H),dispList(T,inter)));
}

implementation sequence over cons of %e determines %e is {
  _cons(H,T) is cons(H,T);
  _apnd(T,H) is append(T,H);
  _empty() from nil;
  _pair(H,T) from cons(H,T);
  _back(nil,E) from cons(E,nil);
  _back(cons(E,B),El) from cons(E,B1) where B1 matches _back(B,El);
  _back(cons(E,B),El) from cons(E,B1) where _back(B,El) bound to B1;
  
  _nil() is nil;
} using {
  append(nil,H) is cons(H,nil);
  append(cons(H,T),E) is cons(H,append(T,E));
}

implementation concatenate over cons of %e is {
  _concat(L,R) is consConc(L,R);
};

implementation updateable over cons of %t determines %t is {
  _extend(L,E) is cons(E,L);
  _merge(L,R) is consConc(L,R);
  _delete(R,P) is removeEls(R,P);
  _update(R,M,U) is updateEls(R,M,U);
} using{
  removeEls(nil,_) is nil;
  removeEls(cons(E,L),P) where E matches P() is removeEls(L,P);
  removeEls(cons(E,L),P) is cons(E,removeEls(L,P));
  
  updateEls(nil,_,_) is nil;
  updateEls(cons(E,L),P,F) where E matches P() is cons(F(E),updateEls(L,P,F));
  updateEls(cons(E,L),P,F) is cons(E,updateEls(L,P,F));
};

private
  consConc(nil,X) is X;
  consConc(cons(H,T),X) is cons(H,consConc(T,X));
  
implementation sorting over cons of %t determines %t is {
  sort(In,C) is let{
    split(nil,P,L,R) is (L,R);
    split(cons(H,T),P,L,R) where C(H,P) is split(T,P,cons(H,L),R);
    split(cons(H,T),P,L,R) default is split(T,P,L,cons(H,R));
    
    qsort(nil) is nil;
    qsort(L matching cons(H,nil)) is L;
    qsort(cons(H,T)) is valof{
      (L,R) is split(T,H,nil,nil);
      valis consConc(qsort(L),cons(H,qsort(R)));
    } 
  } in qsort(In)
};

implementation comparable over cons of %t where comparable over %t 'n equality over %t is {
    X < Y is consLess(X,Y);
    X <= Y is consLessEq(X,Y);
    X > Y is consLess(Y,X);
    X >= Y is consLessEq(Y,X);
  } using {
    consLess(cons of {},cons of {_ ;.. _}) is true;
    consLess(cons of {X;..L1},cons of {X;..L2}) is consLess(L1,L2);
    consLess(cons of {X;.._}, cons of {Y;.._}) where X<Y is true;
    consLess(_,_) default is false;
    
    consLessEq(cons of {},_) is true;
    consLessEq(cons of {X;..L1},cons of {Y;..L2}) where X<=Y is
      consLessEq(L1,L2);
    consLessEq(_,_) default is false;
  };
  
implementation indexable over cons of %e determines (integer,%e) is {
  _index(L,Ix) is consEl(L,Ix,Ix);
  _set_indexed(L,Ix,E) is consSetEl(L,Ix,E);
  _delete_indexed(L,Ix) is consRemove(L,Ix);
} using {    
  consEl(cons(H,_),0,_) is some(H);
  consEl(cons(_,T),N,D) where N>0 is consEl(T,N-1,D);
  consEl(_,_,D) default is none
  
  consSetEl(cons(_,T),0,E) is cons(E,T);
  consSetEl(cons(H,T),N,E) where N>0 is cons(H,consSetEl(T,N-1,E));
  consSetEl(_,_,_) default is raise "index out of range";
  
  consRemove(cons(_,T),0) is T;
  consRemove(cons(H,T),Ix) where Ix>0 is cons(H,consRemove(T,Ix-1));
}

implementation sliceable over cons of %e is {
  _slice(L,Fr,To) is consSlice(L,0,Fr,To);
  _tail(L,Fr) is drop(L,0,Fr);
  _splice(L,Fr,To,Rp) is consSplice(L,0,Fr,To,Rp);
} using {
  consSlice(nil,_,_,_) is nil;
  consSlice(L,Ix,Ix,To) is front(L,Ix,To);
  consSlice(cons(_,T),Ix,Fr,To) is consSlice(T,Ix+1,Fr,To);
  
  front(nil,_,_) is nil;
  front(L,Ixx,Ixx) is nil;
  front(cons(H,T),Ix,To) is cons(H,front(T,Ix+1,To));
  
  consSplice(nil,_,_,_,Rep) is Rep;
  consSplice(L,Fr,Fr,To,Rep) is consConc(Rep,drop(L,Fr,To));
  consSplice(cons(H,T),Ix,Fr,To,Rep) is cons(H,consSplice(T,Ix+1,Fr,To,Rep));
     
  drop(nil,_,_) is nil;
  drop(L,Ix,Ix) is L;
  drop(cons(_,T),Fr,To) is drop(T,Fr+1,To);
} 

implementation sizeable over cons of %e is {
  size(L) is consSize(L);
 
  isEmpty(nil) is true;
  isEmpty(_) default is false;
}

private
  consSize(L) is valof{
    var LL := L;
    var S := 0;
    while LL matches cons(_,Lx) do {
      S := S+1;
      LL := Lx;
    };
    valis S;
  };

implementation equality over cons of %e where equality over %e is {
  (=) = consEq;
} using {
  consEq(nil,nil) is true;
  consEq(cons(H1,T1),cons(H2,T2)) where H1=H2 is consEq(T1,T2);
  consEq(_,_) default is false;
}

implementation iterable over cons of %e determines %e is {
  _iterate(L,F,S) is consIterate(L,F,S);
};

implementation indexed_iterable over cons of %e determines (integer,%e) is {
  _ixiterate(M,F,S) is consIxIterate(M,F,S,0);
}

implementation filterable over cons of %e determines %e is {
  filter(_,nil) is nil;
  filter(P,cons(H,T)) where P(H) is cons(H,filter(P,T));
  filter(P,cons(_,T)) is filter(P,T)
}

private
  consIxIterate(nil,_,St,_) is St;
  consIxIterate(_,_,NoMore(X),_) is NoMore(X);
  consIxIterate(cons(H,T),F,St,Ix) is consIxIterate(T,F,F(Ix,H,St),Ix+1);

private
  consIterate(nil,_,St) is St;
  consIterate(_,_,NoMore(X)) is NoMore(X);
  consIterate(cons(H,T),F,St) is consIterate(T,F,F(H,St));

implementation reversible over cons of %t is {
  reverse(L) is valof{
    var R := nil;
    var LL := L;
  
    while LL matches cons(E,Tl) do{
      R := cons(E,R);
      LL := Tl;
    }

    valis R
  }
}

implementation for all t such that foldable over cons of t determines t is {
  leftFold(F,I,L) is  valof{
    var r := L;
    var st := I;
    while r matches cons(H,T) do {
      st := F(st,H);
      r := T;
    }
    valis st;
  };
  
  leftFold1(F,cons(H,T)) is leftFold(F,H,T);
  leftFold1(F,nil) is raise "list is empty";
  
  rightFold(F,I,nil) is I;
  rightFold(F,I,cons(H,T)) is F(H,rightFold(F,I,T));
  
  rightFold1(F,nil) is raise "list is empty";
  rightFold1(F,L) is rFold1(F,L);
  
  rFold1(F,cons(I,nil)) is I;
  rFold1(F,cons(H,T)) is F(H,rFold1(F,T));
}

implementation mappable over cons is {
  _map(nil,_) is nil;
  _map(cons(H,T),F) is cons(F(H),_map(T,F))
}
