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

private import base
private import strings
private import arithmetic

-- computation is an operator. 
contract (computation) over m determines e is {
  _encapsulate has type for all t such that (t)=>m of t;
  _abort has type for all t such that (e)=>m of t;
  _handle has type for all t such that (m of t, (e)=>m of t) => m of t;
  _combine has type for all s, t such that (m of s,(s)=>m of t)=>m of t
  _delay has type for all t such that (()=>m of t)=>m of t;
  
  fun _delay(F) default is _combine(_encapsulate(()),(_) => F());
}

contract execution over m is {
  _perform has type for all t such that (m of t) => t;
}

contract injection over (m,n) is {
  _inject has type for all t such that (m of t)=>n of t;
}
 
-- default exception handling function
fun raiser_fun(X) is __raise(X);

-- An implementation of 'regular' actions that lie lightly over the implicit monad

type action of t is _delayed(()=>action of t) or _aborted(exception) or _done(t);

fun runCombo(Act,C,H) is valof{
  var A := Act;
  
  while A matches _delayed(D) do
    A := D();
    
  switch A in {
    case _done(X) do valis C(X)
    case _aborted(X) do valis H(X)
    case _ default do raise "illegal case";
  }
}

implementation (computation) over action determines exception is {
  fun _encapsulate(V) is _done(V);
  fun _abort(E) is _aborted(E);
  fun _handle(A,H) is _delayed(() => runCombo(A,_encapsulate,H));
  
  fun _combine(A,C) is _delayed(() => runCombo(A,C,_abort));
  
  fun _delay(F) is _delayed(F);
}
 
implementation execution over action is {
  fun _perform(A) is runCombo(A,id,raiser_fun);
};

implementation injection over (action,action) is {
  fun _inject(C) is C;
}

#action{?A} :: expression :- A ;* action;
#action{} :: expression;
#action{?B} ==> action computation {?B};
#action{} ==> action computation {};
