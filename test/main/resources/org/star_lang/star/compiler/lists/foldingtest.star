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
foldingtest is package{
  -- test the folding stuff
  
  prc main() do {
    def L is cons of [1, 2, 3, 4];
    
    def T is dictionary of [1 -> "one", 2->"two", 3->"three"];
    
    assert rightFold((+),0,L) = leftFold((+),0,L)
    
    logMsg(info,"fold T= $(leftFold((A,(K,V))=>(A++V),"",T))");
    
    assert leftFold((A,(K,V))=>(A++V),"",T)="onetwothree";
    
    logMsg(info,"sub = $(leftFold((-),0,L))");
    logMsg(info,"sub = $(rightFold((-),0,L))");
    
    assert leftFold((-),0,L)=-10;
    
    assert leftFold1((+),L) = 10;
  }
}