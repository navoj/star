/**
 * Define the contracts associated with sequences and collections 
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


public contract sequence over %t determines %e is {
  _empty has type ()<=%t;
  _pair has type (%e,%t)<=%t;
  _cons has type (%e,%t)=>%t;
  _apnd has type (%t,%e)=>%t;
  _back has type (%t,%e)<=%t;
  _nil has type ()=>%t;
}
  
public contract concatenate over t is {
  (++) has type (t,t)=>t;
}
        
public contract indexable over s determines (k,v) is {
  _index has type (s,k)=> option of v;
  _set_indexed has type (s,k,v)=>s;
  _delete_indexed has type (s,k)=>s;
}
  
public contract sliceable over s determines k is {
  _slice has type (s,k,k)=>s;
  _tail has type (s,k)=>s;
  _splice has type (s,k,k,s)=>s;
}

-- implement and handle sequence notation
# ?I of [?C] :: expression :- I::id :& C::sequenceBody; 
# [?C] :: expression :- C::sequenceBody; 

# #( ?S , ?T )# :: sequenceBody :- S::expression :& T::sequenceBody;
# #( ?S ,.. ?T )# :: sequenceBody :- S::sequenceBody :& T::commaCheck;  
# #( ?S .., ?T )# :: sequenceBody :- S::commaCheck :& T::sequenceBody;  
# ?E :: sequenceBody :- E::expression;

# ?L , ?R :: commaCheck :- error("unexpected ','");
# ?E :: commaCheck :- E::expression;

# ?I of [?C] :: pattern :- I::id :& C::sequencePtnBody; 
# [?C] :: pattern :- C::sequencePtnBody; 
# #( ?S , ?T )# :: sequencePtnBody :- S::pattern :& T::sequencePtnBody;
# #( ?S ,.. ?T )# :: sequencePtnBody :- S::sequencePtnBody :& T::pattern;  
# #( ?S .., ?T )# :: sequencePtnBody :- S::pattern :& T::sequencePtnBody;  
# ?E :: sequencePtnBody :- E::pattern;

-- The period gets in the way of the square brackets ... shuffle.

# #(?R)# . #(?F)# [?Ix] ==> #(R.F)#[Ix]
# #(?R)# . #(?F)# [?Ix] := ?Exp  ==> #(R.F)# := _set_indexed(R.F,Ix,Exp)
# #(?M)#[?Ix: ?Cx] ==> _slice(M,Ix,Cx);
# #(?M)#[?Ix:] ==> _tail(M,Ix);
# #(?M)#[?Ix: ?Cx] := ?Exp ==> M := _splice(M,Ix,Cx,Exp);
# _slice(?M,?Ix,?Tx) := ?Exp ==> M := _splice(M,Ix,Tx,Exp);
  
# #(?M)#[?Ix] := ?Exp ==> M := _set_indexed(M,Ix,Exp);
# _index(?M,?Ix) := ?Exp ==> M := _set_indexed(M,Ix,Exp);

# remove #(?M)#[?Ix] ==> M := _delete_indexed(M,Ix);
# remove _index(?M,?Ix) ==> M := _delete_indexed(M,Ix);
# #(?M)#[?Ky->?Vl] ==> _set_indexed(M,Ky,Vl);
# #(?M)#[with ?Ky->?Vl] ==> _set_indexed(M,Ky,Vl);
# _index(?M,?Ky->?Vl) ==> _set_indexed(M,Ky,Vl);
# _index(?M,with ?Ky->?Vl) ==> _set_indexed(M,Ky,Vl);
# #(?M)#[without ?Ky] ==> _delete_indexed(M,Ky);
# _index(?M,without ?Ky) ==> _delete_indexed(M,Ky);

# #(?M)#[?Ix] ==> _index(M,Ix);
  
public contract sorting over %c determines %t is {
  sort has type (%c,(%t,%t)=>boolean)=>%c
}
  
public contract reversible over t is {
  reverse has type (t)=>t;
}

public contract explosion over (t,c) is {
  explode has type (t)=>c;
  implode has type (c)=>t;
}