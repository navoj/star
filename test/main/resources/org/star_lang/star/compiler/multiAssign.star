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
multiAssign is package{

  t2(A,B) is (A,B);
  
  main() do {    
    -- test multiple assignment
    var A := "fred";
    var B := 2;
    
    assert A="fred";
    assert B=2;
    
    (A,B) := ("peter",B)
    
    assert A="peter" and B=2;
    
    (A,B) := t2("john",3);
    
    assert A="john" and B=3;
  }
}