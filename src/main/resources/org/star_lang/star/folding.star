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
private import iterable;

contract mappable over c is {
  map has type for all e,f such that ((e)=>f,c of e) => c of f;
}

contract filterable over t determines e is {
  filter has type ((e)=>boolean,t) => t
}

-- This has to be here, to avoid circular packages

implementation filterable over string determines integer is {
  fun filter(P,string(S)) is string(__string_filter(S,P))
   |  filter(_,nonString) is nonString;
}

contract foldable over c determines e is {
  leftFold has type for all st such that ((st,e)=>st,st,c)=>st;
  leftFold1 has type ((e,e)=>e,c) => e;
  rightFold has type for all st such that ((e,st)=>st,st,c)=>st;
  rightFold1 has type ((e,e)=>e,c)=>e;
  
  fun leftFold1(F,C) default is let{
    fun razer() is raise "problem"
    fun leftState(NoneFound,E) is ContinueWith(E)
     |  leftState(ContinueWith(St),E) is ContinueWith(F(E,St))
     |  leftState(X,_) default is X
  } in _checkIterState(leftFold(leftState,NoneFound,C),razer);
  
  fun rightFold1(F,C) default is let{
    fun razer() is raise "problem"
    fun rightState(E,NoneFound) is ContinueWith(E)
     |  rightState(E,ContinueWith(St)) is ContinueWith(F(St,E))
     |  rightState(_,X) default is X
  } in _checkIterState(rightFold(rightState,NoneFound,C),razer);
}