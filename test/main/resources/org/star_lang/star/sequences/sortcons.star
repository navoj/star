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
sortcons is package{
  prc main() do {
    def S is cons of [("alpha",1), ("beta",2), ("alpha",0), ("beta",10), ("gamma",1)]
    
    logMsg(info,"Sort $S to\n$(sort(S,<))");
    
    assert sort(S,<) = cons of [("alpha",0), ("alpha",1), ("beta",2), ("beta",10), ("gamma",1)];
    
    PS has type cons of integer;
    def PS is iota(1,300,1);
    
    logMsg(info,"positive cons = $PS");
    
    CS has type cons of integer;
    def CS is iota(300,1,-1);
    logMsg(info,"negative cons = $CS");
    
    
    def SS is sort(CS,<);
    
    logMsg(info,"sorted is $SS");
    
    assert SS = PS;
  }
}