/**
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
maybeTest is package{
  import maybe;
  
  ff(K,L) is maybe computation{
    for (KK,V) in L do{
      if K=KK then
        valis V;
    };
    raise "not found";
  };
  
  id(X) is X;
  
  main() do {
    MM is list of {(1,"alpha"); (2,"beta"); (3,"gamma"); (4,"delta")};
    
    logMsg(info,"value of ff(2,MM) is $(valof ff(2,MM))");
    
    assert valof ff(2,MM) = "beta"
    
    R2 is valof ff(5,MM) on abort (function(exception(_,E,_)) is E cast string);
    
    logMsg(info,"value of ff(5,MM) is $(R2)");

    assert R2 = "not found";
  }
} 