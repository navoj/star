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
private import casting;
private import strings;
private import sequences;
private import compute;

type IterState of t is NoneFound or NoMore(t) or ContinueWith(t) or AbortIter(exception);

-- The iterable contract is used in planning queries
-- The iterate function takes a filter function and iterates over the collection using it while it returns a IterState state 

contract iterable over s determines e is {
  _iterate has type for all r such that (s,(e,IterState of r)=>IterState of r,IterState of r) => IterState of r;
}

contract indexed_iterable over s determines (k,v) is {
  _ixiterate has type for all r such that (s,(k,v,IterState of r)=>IterState of r,IterState of r) => IterState of r;
}

type _possible of t is _impossible or _possible(t);

fun _optionIterState(NoMore(X)) is some(X)
 |  _optionIterState(ContinueWith(X)) is some(X)
 |  _optionIterState(NoneFound) is none

fun _checkIterState(NoMore(X),_) is X
 |  _checkIterState(ContinueWith(X),_) is X
 |  _checkIterState(NoneFound,D) is D()

fun _negate(NoMore(true),A,B) is A()
 |  _negate(ContinueWith(true),A,B) is A()
 |  _negate(_,A,B) default is B()

fun _otherwise(NoneFound,F) is F()
 |  _otherwise(ContinueWith(_empty()),F) is F()
 |  _otherwise(St,_) default is St

fun _project_0_2((L,_)) is L;

-- We have to put this here because we need to import strings

implementation iterable over string determines char is {
  fun _iterate(M,F,S) is __string_iter(M,F,S)
}

implementation indexed_iterable over string determines (integer,char) is {
  fun _ixiterate(string(Str),F,S) is __string_ix_iterate(Str,F,S)
}

contract grouping over coll determines (m,k,v) where indexable over m of (k,coll of v) determines (k,coll of v) is {
  (group by) has type ((coll of v), (v)=>k) => m of (k,coll of v)
}

implementation (computation) over IterState determines exception is {
  fun _encapsulate(X) is ContinueWith(X)
  fun _combine(NoneFound,F) is NoneFound
   |  _combine(ContinueWith(X),F) is F(X)
   |  _combine(NoMore(X),F) is F(X)
   |  _combine(AbortIter(x),_) is AbortIter(x)
  fun _handle(AbortIter(x),E) is E(x)
   |  _handle(V,_) default is V
  fun _abort(E) is AbortIter(E)
}


