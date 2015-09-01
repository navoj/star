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

-- compare and swap
atomic_int_cas has type (atomic_int, integer_, integer_) => integer_
fun atomic_int_cas(ar, expected, upd) is valof {
  while (true) do {
    var curr := __atomic_int_reference(ar);
    if __atomic_int_test_n_set(ar, expected, upd) then
      valis expected -- expected equals current
    else if not __integer_eq(curr, expected) then
      valis curr;
    -- else loop - value must have changed between deref and swap
    -- logMsg(info, "cas: current was $(__display(curr)), setting from $(__display(expected)) to $(__display(upd)) failed");
  }
}

-- referential equality
atomic_int_ref_eq has type (atomic_int, atomic_int) => boolean
fun atomic_int_ref_eq(r1, r2) is __equal(r1, r2)