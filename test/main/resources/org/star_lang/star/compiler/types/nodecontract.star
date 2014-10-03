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
nodecontract is package{
  contract foo over %t is {
    bar has type (%t)=>integer;
  }
  
  implementation foo over integer is {
    bar(X) is X*2;
  }
  
  type tree is node{
    tt has kind type;
    XX has type tt where foo over tt;
    
    B has type (tt)=>integer;
  };
  
  main() do {
    N is node{
      type tt is alias of integer;
      XX is 23;
      B(X) is bar(X);
    };
    
    B is N.B(N.XX);
    logMsg(info,"B=$B");
    assert B=46;
  }
}