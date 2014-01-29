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
casTest is package{
  private import atomics;
  
  -- test the test_and_swap
  
  main() do {
    I is 34;
    A is atomic(I);
    
    assert __atomic_test_n_set(A,I,56);
    
    assert _get(A)=56;
    
    assert not __atomic_test_n_set(A,I,23);
    
    assert _get(A)=56;
    
    -- test integer atomics
    
    II has type atomic_int;
    II is _atomic(34);
    
    IR is 56_;
    
    assert __atomic_int_test_n_set(II,34_,IR);
    
    assert _get(II)=56;
    
    assert not __atomic_int_test_n_set(II,34_,23_);
    
    assert _get(II)=56;
  }
}