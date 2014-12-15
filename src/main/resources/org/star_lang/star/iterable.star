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
private import casting;
private import strings;
private import sequences;

type IterState of t is NoneFound or NoMore(t) or ContinueWith(t) or AbortIter(exception);

-- The iterable contract is used in planning queries
-- The iterate function takes a filter function and iterates over the collection using it while it returns a IterState state 

contract iterable over %s determines %e is {
  _iterate has type for all %r such that (%s,(%e,IterState of %r)=>IterState of %r,IterState of %r) => IterState of %r;
}

contract indexed_iterable over %s determines (%k,%v) is {
  _ixiterate has type for all %r such that (%s,(%k,%v,IterState of %r)=>IterState of %r,IterState of %r) => IterState of %r;
}

type _possible of t is _impossible or _possible(t);

_checkIterState(NoMore(X),_) is X;
_checkIterState(ContinueWith(X),_) is X;
_checkIterState(NoneFound,D) is D();

_negate(NoMore(true),A,B) is A();
_negate(ContinueWith(true),A,B) is A();
_negate(_,A,B) default is B();

_otherwise(NoneFound,F) is F();
_otherwise(ContinueWith(_empty()),F) is F();
_otherwise(St,_) default is St;

_project_0_2((L,_)) is L;

-- We have to put this here because we need to import strings

implementation iterable over string determines char is {
  _iterate(M,F,S) is __string_iter(M,F,S);
}

implementation indexed_iterable over string determines (integer,char) is {
  _ixiterate(string(Str),F,S) is __string_ix_iterate(Str,F,S);
}

contract grouping over coll determines (m,k,v) where indexable over m of (k,coll of v) determines (k,coll of v) is {
  (group by) has type ((coll of v), (v)=>k) => m of (k,coll of v)
}