/**
 * Implement a range operator 
 *
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
private import arithmetic;
private import iterable;
private import sequences;
private import strings;
private import casting;
private import folding;

type range of t where arithmetic over t and comparable over t is range(t,t,t);

implementation sizeable over range of %t where coercion over (%t,integer) is {
  fun isEmpty(range(F,T,_)) is F>=T;
  fun size(range(F,T,S)) is ((T-F)/S) as integer;
}

implementation iterable over range of %t determines %t is {
  fun _iterate(range(Fr,To,Stp),Fn,St) is iotaIterate(Fr,To,Stp,Fn,St);
}

private fun iotaIterate(Fr,To,Stp,Fn,S) is valof{
  var St := S;
  var Ix := Fr;
  
  while Ix/Stp<To/Stp do{
    if St matches NoMore(X) then
      valis NoMore(X) -- cannot just use St 'because of handling of computation expressions
    else
      St := Fn(Ix,St);
    Ix := Ix+Stp;
  }
  valis St;
}
/*

implementation for all t such that sequence over range of t determines t where coercion over (integer,t) and equality over t is {
    ptn _empty() from range(F,T,I) where F*I>=T*I
    ptn _pair(F,range(F+I,T,I)) from range(F,T,I) where F*I<T*I;
    fun _cons(H,range(F,T,I)) where F*I>=T*I is range(H,H+I,I)
     |  _cons(H,range(F,T,I)) where F-I=H is range(H,T,I);
    fun _apnd(range(F,T,I),E) where F*I>=T*I is range(E,E+I,I)
     |  _apnd(range(F,T,I),E) where T+I=E is range(F,E,I);
    ptn _back(range(F,E-I,I),E) from range(F,E,I);
    fun _nil() is range(0 as t,0 as t,1 as t);
  }
*/

implementation for all t such that foldable over range of t determines t is {
  fun leftFold(F,I,range(Fr,To,Inc)) is  valof{
    var r := Fr
    def limit is To*Inc
    var st := I;
    while r*Inc< limit do {
      st := F(st,r);
      r := r+Inc
    }
    valis st;
  };
    
  fun rightFold(F,I,range(Fr,To,Inc)) is  valof{
    var r := To
    def limit is Fr*Inc
    var st := I;
    while r*Inc > limit do {
      r := r-Inc
      st := F(r,st);
    }
    valis st;
  };
    
  fun leftFold1(F,range(Fr,To,Inc)) where Fr*Inc<To*Inc is leftFold(F,Fr,range(Fr+Inc,To,Inc))
   |  leftFold1(F,_) is raise "range is empty";
  

  fun rightFold1(F,range(Fr,To,Inc)) where Fr*Inc<To*Inc is rightFold(F,To,range(Fr,To-Inc,Inc))
   |  rightFold1(F,_) is raise "range is empty";
}

implementation for all t such that concatenate over range of t where equality over t is {
  fun range(Fr,Md,Inc)++range(Md,To,Inc) is range(Fr,To,Inc);
}

-- macro out common use cases ...
# #(for #(identifier?C)# in range(?S,?L,1) do ?A)# ==> {
  var C := S;
  while C < L do{
    A;
    C := C+1;
  }
};
# #(for #(identifier?C)# in range(?S,?L,?Stp) do ?A)# ==> {
  var C := S;
  while C/Stp < L/Stp do{
    A;
    C := C+Stp;
  }
};