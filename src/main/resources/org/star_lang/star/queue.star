/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

private import base;
private import sequences;
private import iterable;
private import strings;
private import cons;
private import arithmetic;
private import updateable;

public type queue of %t is queue{
  front has type cons of %t;
  back has type cons of %t
}

public implementation sequence over queue of %e determines %e is {
  ptn _empty() from queue{front=nil; back=nil};
  fun _nil() is queue{front=nil; back=nil};
  
  ptn _pair(E,queue{front=F;back=B}) from queue{front = cons(E,F); back = B}
   |  _pair(E,queue{front=B;back=nil}) from queue{front=nil; back=Bk} where reverse(Bk) matches backQ(E,B);
  
  fun _cons(H,queue{front=F;back=B}) is queue{front=cons(H,F);back=B};
  
  fun _apnd(queue{front=F;back=B},E) is queue{front=F;back=cons(E,B)};
  
  ptn _back(queue{front=F;back=B},E) from queue{front = F; back = cons(E,B)}
   |  _back(queue{front=nil;back=F},E) from queue{front=Fr; back=nil} where reverse(Fr) matches backQ(E,F);
} using {
  ptn backQ(E,Q) from _pair(E,Q);
}

public implementation equality over queue of %t where equality over %t is {
  (=) = qEqual;
  hashCode = qHash
} using {
  fun qEqual(queue{front=F1;back=B1},queue{front=F2;back=B2}) is F1++reverse(B1)=F2++reverse(B2);
  fun qHash(queue{front=F;back=B}) is hashCode(F++reverse(B))
};

public implementation sizeable over queue of %t is {
  fun size(Q) is queueSize(Q);
  
  fun isEmpty(queue{front=nil;back=nil}) is true
   |  isEmpty(_) default is false;
} using {
  fun queueSize(queue{front=F;back=B}) is size(F)+size(B)
}

public implementation iterable over queue of %e determines %e is {
  fun _iterate(queue{front=F;back=B},Fn,St) is consIterate(F++reverse(B),Fn,St);
}

private
  fun consIterate(nil,_,St) is St
   |  consIterate(_,_,NoMore(X)) is NoMore(X)
   |  consIterate(cons(H,T),F,St) is consIterate(T,F,F(H,St));
  
public implementation concatenate over queue of %e is {
  fun L++R is queueConc(L,R);
} using {
  fun queueConc(queue{front=F1;back=B1},queue{front=F2;back=B2}) is queue{front=F1++reverse(B1); back=B2++reverse(F2)}
}

public implementation updateable over queue of %t determines %t is {
  fun _extend(queue{front=F;back=B},E) is queue{front=F;back=cons(E,B)};
  fun _merge(queue{front=F1;back=B1},queue{front=F2;back=B2}) is queue{front=F1++F2;back=B2++B1};
  fun _delete(queue{front=F;back=B},P) is queue{front=removeEls(F,P); back=removeEls(B,P)};
  fun _update(queue{front=F;back=B},M,U) is queue{front=updateEls(F,M,U); back=updateEls(B,M,U)};
} using{
  fun removeEls(nil,_) is nil
   |  removeEls(cons(E,L),P) where E matches P() is removeEls(L,P)
   |  removeEls(cons(E,L),P) is cons(E,removeEls(L,P));
  
  fun updateEls(nil,_,_) is nil
   |  updateEls(cons(E,L),P,F) where E matches P() is cons(F(E),updateEls(L,P,F))
   |  updateEls(cons(E,L),P,F) is cons(E,updateEls(L,P,F));
};

public implementation reversible over queue of %t is {
  fun reverse(Q) is revQ(Q)
} using {
  fun revQ(queue{front=F;back=B}) is queue{front=reverse(B);back=reverse(F)}
}