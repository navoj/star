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

contract updates over %t determines %e is {
  _assign_ has type action(%t);
  _replace_ has type action(ref %t,(%e)=>%e);
};

# update ?Ptn in ?Tgt with ?Exp :: action ==> let{
    fun #$filter(Ptn) is Exp
    | #$filter(#$X) default is #$X; 
  } in _replace_(Tgt, #$filter);

