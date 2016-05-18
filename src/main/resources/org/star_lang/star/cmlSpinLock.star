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
private import strings;
private import arithmetic;

private def __locked_v is 1_;
private def __unlocked_v is 0_;

type spin_lock is spin_lock {
  locked has type atomic_int;
}

mk_spin_lock has type () => spin_lock
fun mk_spin_lock() is spin_lock {
  locked = atomic_int(__unlocked_v);
}

spinLock has type (spin_lock)=>()
prc spinLock(l) do {
  -- it's usually better to first try with a normal memory read, and then with the CAS
  var done := false;
  while not done do {
    -- yield while locked
    while (__integer_eq(__atomic_int_reference(l.locked), __locked_v)) do {
	    __yield();
    }
    
     -- try to lock
    done := __atomic_int_test_n_set(l.locked, __unlocked_v, __locked_v);
  }
}

spinUnlock has type (spin_lock)=>()
prc spinUnlock(l) do {
  __atomic_int_assign(l.locked, __unlocked_v);
}