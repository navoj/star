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
trace is package{
  import compute;
  
  #trace{ ?A } :: expression :- A ;* action;
  #trace { ?B } ==> trace computation { ?B };
  
   -- this is a temporary test
  type trace of %t is xx(%t) or drop(exception) or zz(()=>%t);
  
  implementation (computation) over trace is {
    fun _encapsulate(x) is valof{ logMsg(info,"encap $x"); valis xx(x)};
    fun _combine(m, f) is switch m in {
      case drop(S) is drop(S);
      case xx(v) is f(v);
    };
    fun _abort(S) is drop(S);
    
    fun _handle(drop(M),EF) is valof{logMsg(info,"drop: $M"); valis EF(M)}
     |  _handle(R,_) is R;
    
  }
  
  implementation execution over trace is {
    fun _perform(xx(X),_) is X
     |  _perform(drop(MSG),EF) is EF(MSG)
  }
}