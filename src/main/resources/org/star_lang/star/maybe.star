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
private import strings;
private import base;
private import compute;

type maybe of %t is possible(%t) or impossible(exception);

implementation (computation) over maybe is {
  _encapsulate(X) is possible(X);
    
  _combine(possible(X),F) is F(X);
  _combine(impossible(E),_) is impossible(E);
    
  _abort(R) is impossible(R);
    
  _handle(impossible(R),EF) is EF(R);
  _handle(M,EF) is M;
}
  
implementation execution over maybe is {
    _perform(possible(X),_) is X;
    _perform(impossible(R),F) is F(R);
}