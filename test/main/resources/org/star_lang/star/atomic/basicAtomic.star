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
basicAtomic is package{

  private import atomics;
  
  main() do {
    A is atomic(34);
    
    assert _get(A)=34;
    
    _assign(A,45);
    
    assert _get(A)=45;
    
    I has type atomic_int;
    I is _atomic(23);
    
    assert _get(I)=23;
    
    _assign(I,45);
    
    assert _get(I)=45;
  }
}