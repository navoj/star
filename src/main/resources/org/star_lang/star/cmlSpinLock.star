/**
 * 
 * Copyright (C) 2013 Starview Inc
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
private import base;
private import strings;


private  
__locked_v is 1_;
private
__unlocked_v is 0_;

type spin_lock is spin_lock {
  locked has type atomic_int;
}

mk_spin_lock has type () => spin_lock
mk_spin_lock() is spin_lock {
  locked = atomic_int(__unlocked_v);
}

spinLock has type action(spin_lock)
spinLock(l) do {
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

spinUnlock has type action(spin_lock)
spinUnlock(l) do {
  __atomic_int_assign(l.locked, __unlocked_v);
}