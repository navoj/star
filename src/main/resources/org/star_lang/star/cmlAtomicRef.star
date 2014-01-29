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

-- compare and swap
atomic_int_cas has type (atomic_int, _integer, _integer) => _integer
atomic_int_cas(ar, expected, upd) is valof {
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
atomic_int_ref_eq(r1, r2) is __equal(r1, r2)