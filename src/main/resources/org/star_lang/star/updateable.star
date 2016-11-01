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
public contract updateable over %r determines %t is {
  _extend has type (%r,%t)=>%r;
  _merge has type (%r, %r) => %r;
  _delete has type (%r, ()<=%t) => %r;
  _update has type (%r, ()<=%t, (%t)=>%t) => %r;
}

-- Update sub-language
# update ?Ptn in ?Tgt with ?Exp :: action :- Ptn::pattern :& Tgt::lvalue :& Exp::expression;
# delete ?Ptn in ?Tgt :: action :- Ptn::pattern :& Tgt::expression;
# extend ?Tgt with ?Exp :: action :- Tgt::lvalue :& Exp::expression;
# merge ?Tgt with ?Exp :: action :- Tgt::lvalue :& Exp::expression;

# extend ?Tgt with ?Exp ==> Tgt := _extend(Tgt,Exp);
# merge ?Tgt with ?Exp ==> Tgt := _merge(Tgt, Exp);
# delete ?Ptn in ?Tgt ==> Tgt := _delete(Tgt, (() from Ptn));
# update ?Ptn in ?Tgt with ?Exp ==> Tgt := _update(Tgt, (() from Ptn), (Ptn) => Exp);
