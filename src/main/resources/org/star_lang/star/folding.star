/**
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
private import iterable;

contract mappable over c is {
  map has type for all e,f such that ((e)=>f,c of e) => c of f;
}

contract filterable over t determines e is {
  filter has type ((e)=>boolean,t) => t
}

-- This has to be here, to avoid circular packages

implementation filterable over string determines char is {
  filter(P,string(S)) is string(__string_filter(S,P));
  filter(_,nonString) is nonString;
}

contract foldable over c determines e is {
  leftFold has type for all st such that ((st,e)=>st,st,c)=>st;
  leftFold1 has type ((e,e)=>e,c) => e;
  rightFold has type for all st such that ((e,st)=>st,st,c)=>st;
  rightFold1 has type ((e,e)=>e,c)=>e;
  
  leftFold1(F,C) default is let{
    razer() is raise "problem";
    leftState(NoneFound,E) is ContinueWith(E);
    leftState(ContinueWith(St),E) is ContinueWith(F(E,St));
    leftState(X,_) default is X;
  } in _checkIterState(leftFold(leftState,NoneFound,C),razer);
  
  rightFold1(F,C) default is let{
    razer() is raise "problem";
    rightState(E,NoneFound) is ContinueWith(E);
    rightState(E,ContinueWith(St)) is ContinueWith(F(St,E));
    rightState(_,X) default is X;
  } in _checkIterState(rightFold(rightState,NoneFound,C),razer);
}