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
pick is package{
  -- sample programs to check out the match compiler with

  pick has type (list of %t,integer) => %t;
  fun pick(list of [X,.._],0) is X
   |  pick(list of [_,X,.._],1) is X
   |  pick(list of [_,_,X,.._],2) is X
   |  pick(list of [_,_,_,..R],N) is pick(R,N-3)
  
  main has type action();
  prc main() do {
    def L is list of [1,2,3,4,5,6];
    logMsg(info, "pick 3rd from $L = $(pick(L,2))");
    logMsg(info, "second element is $(L[1])");
    
    assert pick(L,4)=5;
    assert pick(L,2)=3;
  };
  
} 