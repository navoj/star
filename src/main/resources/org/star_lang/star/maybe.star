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

private import strings;
private import base;
private import arithmetic;
private import compute;

public type maybe of %t is possible(%t) or impossible(exception);

public implementation (computation) over maybe determines exception is {
  fun _encapsulate(X) is possible(X)
    
  fun _combine(possible(X),F) is F(X)
   |  _combine(impossible(E),_) is impossible(E)
    
  fun _abort(R) is impossible(R)
    
  fun _handle(impossible(R),EF) is EF(R)
   |  _handle(M,EF) is M
}
  
public implementation execution over maybe is {
    fun _perform(possible(X)) is X
}