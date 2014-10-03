/**
 * define the computation contracts. 
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
private import strings;

-- computation is an operator. 
contract (computation) over %%c is {
  _encapsulate has type for all %t such that (%t)=>%%c of %t;
  _abort has type for all %t such that (exception)=>%%c of %t;
  _handle has type for all %t such that (%%c of %t, (exception)=>%%c of %t) => %%c of %t;
  _combine has type for all %s, %t such that (%%c of %s,(%s)=>%%c of %t)=>%%c of %t
  _delay has type for all %t such that (()=>%%c of %t)=>%%c of %t;
  
  _delay(F) default is _combine(_encapsulate(()),(function(_) is F()));
}

contract execution over %%c is {
  _perform has type for all t such that (%%c of t,(exception)=>t) => t;
}

contract injection over (%%m,%%n) is {
  _inject has type for all %t such that (%%m of %t)=>%%n of %t;
}
 
-- default exception handling function
raiser_fun(X) is __raise(X);

-- An implementation of 'regular' actions that lie lightly over the implicit monad

type action of t is _delayed(()=>action of t) or _aborted(exception) or _done(t);

runCombo(Act,C,H) is valof{
  var A := Act;
  
  while A matches _delayed(D) do
    A := D();
    
  case A in {
    _done(X) do valis C(X);
    _aborted(X) do valis H(X);
    _ default do raise "illegal case";
  }
}

implementation (computation) over action is {
  _encapsulate(V) is _done(V);
  _abort(E) is _aborted(E);
  _handle(A,H) is _delayed((function() is runCombo(A,_encapsulate,H)));
  
  _combine(A,C) is _delayed((function() is runCombo(A,C,_abort)));
  
  _delay(F) is _delayed(F);
}
 
implementation execution over action is {
  _perform(A,H) is runCombo(A,id,H);
};

implementation injection over (action,action) is {
  _inject(C) is C;
}

#action{?A} :: expression :- A ;* action;
#action{} :: expression;
#action{?B} ==> action computation {?B};
#action{} ==> action computation {};
