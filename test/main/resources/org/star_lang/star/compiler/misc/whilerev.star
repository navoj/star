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
whilerev is package{
  reverse(L) is valof{
    var r := nil;
    var l := L;
    while l matches cons(H,T) do{
      r := cons(H,r);
      l := T;
    };
    valis r;
  };
  
  conc(L,R) is valof{
    var l := reverse(L);
    var r := R;
    while l matches cons(H,T) do{
      r := cons(H,r);
      l := T;
    };
    valis r;
  }

  main() do {
    L is cons of {1;2;3;4;5};
    R is cons of {6;7;8};
    assert reverse(L)=cons of {5;4;3;2;1};
    
    assert conc(L,R) = cons of {1;2;3;4;5;6;7;8};
  }
}