/**
 * Define the contracts associated with sequences and collections 
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

contract sequence over %t determines %e is {
  _empty has type ()<=%t;
  _pair has type (%e,%t)<=%t;
  _cons has type (%e,%t)=>%t;
  _apnd has type (%t,%e)=>%t;
  _back has type (%t,%e)<=%t;
  _nil has type ()=>%t;
}
  
contract concatenate over t is {
  (++) has type (t,t)=>t;
}
        
contract indexable over s determines (k,v) is {
  _index has type (s,k)=> option of v;
  _set_indexed has type (s,k,v)=>s;
  _delete_indexed has type (s,k)=>s;
}
  
contract sliceable over s determines k is {
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
  
# prefix((present),500);
# present #(?M)#[?Ix] :: expression :- M::expression :& Ix::expression;
# present #(?M)# . #(?F)# [?Ix] ==> _index(M.F,Ix) matches some(_);
# present #(?M)#[?Ix] ==> _index(M,Ix) matches some(_);

# ?O. #(?M)#[?K] matches ?V ==> _index(O.M,K) matches some(V);
# #(?M)#[?K] matches ?V ==> _index(M,K) matches some(V);
  
contract sorting over %c determines %t is {
  sort has type (%c,(%t,%t)=>boolean)=>%c
}
  
contract reversible over t is {
  reverse has type (t)=>t;
}
  
#right((union),700);
#right((intersect),600);
#right((complement),500);
  
contract sets over t is {
  (union) has type (t,t)=>t;
  (intersect) has type (t,t)=>t;
  (complement) has type (t,t)=>t;
}

contract explosion over (t,c) determines e is {
  explode has type (t)=>c of e;
  implode has type (c of e) => t;
}