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
tasking is package{
  import compute;
  
  #task{?A} :: expression :- A ;* action;
  #task { ?B } ==> task computation {?B};
  
   -- this is a temporary test
  type task of %t is xx(%t) or drop(string);
  
  implementation (computation) over task is {
    _encapsulate(x) is xx(x);
    _combine(m, f) is case m in {
      drop(S) is drop(S);
      xx(v) is f(v);
    };
    _abort(S) is drop(S);
    
    _handle(drop(M),EF) is EF(M);
    _handle(O,_) is O;
  }
  
  implementation execution over task is {
    _perform(xx(X),_) is X;
    _perform(drop(MSG),EF) is EF(MSG);
  }
}