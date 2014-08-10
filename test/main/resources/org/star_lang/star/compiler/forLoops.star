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
forLoops is package{
  -- test out various for loops
    
  L is list of {1;2;3;4;5};
  
  C is cons of {1;2;3;4;5};
    
  M is dictionary of {1->"1"; 2->"2"; 3->"3"; 4->"4"; 5->"5"};
  
  main() do {
    -- basic loops
 
    for l in L do
      logMsg(info,"l=$l");
 
    for c in C do
      logMsg(info,"c=$c");
 
    for k->v in M do
      logMsg(info,"k=$k,v=$v");
      
    -- index loops
    
    for Ix->l in L do{
      logMsg(info,"l=$l, Ix=$Ix");
      assert l=Ix+1
    };
    
    for Ix->a in C do{
      logMsg(info,"a=$a, Ix=$Ix");
      assert a=Ix+1
    };
    
    for k->v in M do{
      logMsg(info,"v=$v, k=$k");
    }
  }
}
