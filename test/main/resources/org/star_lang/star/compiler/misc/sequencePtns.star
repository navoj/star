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
sequencePtns is package{
  -- test out the use of <lbl> of {<el> ; .. ; <el>} patterns
  
  find(sequence of {},_) is false;
  find(sequence of {X;..Y},X) is true;
  find(sequence of {_;..Y},X) is find(Y,X);
  
  atEnd(sequence of {},_) is false;
  atEnd(sequence of {X},X) is true;
  atEnd(sequence of {_;..Y},X) is atEnd(Y,X);
  
  L is cons of {"alpha"; "beta"; "gamma" };
  
  walk(sequence of {X;..Y}) do
    {
      logMsg(info,"got $X");
      walk(Y);
    }
  walk(sequence of {}) do {};
  
  main() do {
    assert find(L,"alpha");
    assert find(L,"beta");
    assert find(L,"gamma");
    
    assert not find(L,"one");
    
    assert atEnd(L,"gamma");
    assert not atEnd(L,"alpha");
    assert not atEnd(L,"beta");
    assert not atEnd(L,"one");
    
    walk(L);
  }
}