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
konspp is package{

  type kk of %t is kn or kkons(%t,kk of %t);
  
  implementation sequence over kk of %t determines %t is {
    _cons(H,T) is kkons(H,T);
    _apnd(T,H) is konc(T,H);
    _empty() from kn;
    _pair(H,T) from kkons(H,T);
    _back(kn,E) from kkons(E,kn);
    _back(kkons(H,B),E) from kkons(H,B1) where B1 matches _back(B,E);
    _nil() is kn;
  } using {
    konc(kn,H) is kkons(H,kn)
    konc(kkons(H,T),E) is kkons(H,konc(T,E));
  }
  
  main() do {
    logMsg(info,"try printing a sequence: $(kk of {1;2;3;4})");
    assert display(kk of {1;2;3;4}) = "kk of {1;2;3;4}";
  }
}