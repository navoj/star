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
  -- test out the use of <lbl> of [<el> , .. , <el>] patterns
  
  find([],_) is false;
  find([X,.._],X) is true;
  find([_,..Y],X) is find(Y,X);
  
  atEnd(cons of [],_) is false;
  atEnd(cons of [X],X) is true;
  atEnd(cons of [_,..Y],X) is atEnd(Y,X);
  
  L is cons of ["alpha", "beta", "gamma" ];
  
  walk(cons of [X,..Y]) do
    {
      logMsg(info,"got $X");
      walk(Y);
    }
  walk(cons of []) do {};
  
  main() do {
    logMsg(info,"L=$L");
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