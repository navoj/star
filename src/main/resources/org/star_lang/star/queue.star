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
private import iterable;
private import strings;
private import cons;
private import arithmetic;
private import updateable;

type queue of %t is queue{
  front has type cons of %t;
  back has type cons of %t
}

implementation sequence over queue of %e determines %e is {
  _empty() from queue{front=nil; back=nil};
  _nil() is queue{front=nil; back=nil};
  
  _pair(E,queue{front=F;back=B}) from queue{front = cons(E,F); back = B};
  _pair(E,queue{front=B;back=nil}) from queue{front=nil; back=Bk} where reverse(Bk) matches backQ(E,B);
  
  _cons(H,queue{front=F;back=B}) is queue{front=cons(H,F);back=B};
  
  _apnd(queue{front=F;back=B},E) is queue{front=F;back=cons(E,B)};
  
  _back(queue{front=F;back=B},E) from queue{front = F; back = cons(E,B)};
  _back(queue{front=nil;back=F},E) from queue{front=Fr; back=nil} where reverse(Fr) matches backQ(E,F);
} using {
  backQ(E,Q) from _pair(E,Q);
}

implementation equality over queue of %t where equality over %t is {
  (=) = qEqual;
} using {
  qEqual(queue{front=F1;back=B1},queue{front=F2;back=B2}) is F1++reverse(B1)=F2++reverse(B2);
};

implementation sizeable over queue of %t is {
  size(Q) is queueSize(Q);
  
  isEmpty(queue{front=nil;back=nil}) is true;
  isEmpty(_) default is false;
} using {
  queueSize(queue{front=F;back=B}) is size(F)+size(B)
}

implementation iterable over queue of %e determines %e is {
  _iterate(queue{front=F;back=B},Fn,St) is consIterate(F++reverse(B),Fn,St);
}

private
  consIterate(nil,_,St) is St;
  consIterate(_,_,NoMore(X)) is NoMore(X);
  consIterate(cons(H,T),F,St) is consIterate(T,F,F(H,St));
  
implementation concatenate over queue of %e is {
  L++R is queueConc(L,R);
} using {
  queueConc(queue{front=F1;back=B1},queue{front=F2;back=B2}) is queue{front=F1++reverse(B1); back=B2++reverse(F2)}
}

implementation updateable over queue of %t determines %t is {
  _extend(queue{front=F;back=B},E) is queue{front=F;back=cons(E,B)};
  _merge(queue{front=F1;back=B1},queue{front=F2;back=B2}) is queue{front=F1++F2;back=B2++B1};
  _delete(queue{front=F;back=B},P) is queue{front=removeEls(F,P); back=removeEls(B,P)};
  _update(queue{front=F;back=B},M,U) is queue{front=updateEls(F,M,U); back=updateEls(B,M,U)};
} using{
  removeEls(nil,_) is nil;
  removeEls(cons(E,L),P) where E matches P() is removeEls(L,P);
  removeEls(cons(E,L),P) is cons(E,removeEls(L,P));
  
  updateEls(nil,_,_) is nil;
  updateEls(cons(E,L),P,F) where E matches P() is cons(F(E),updateEls(L,P,F));
  updateEls(cons(E,L),P,F) is cons(E,updateEls(L,P,F));
};

implementation reversible over queue of %t is {
  reverse(Q) is revQ(Q)
} using {
  revQ(queue{front=F;back=B}) is queue{front=reverse(B);back=reverse(F)}
}