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
strsequence is package{

  shChar has type (char,IterState of cons of char)=>IterState of cons of char;
  shChar(X,ContinueWith(st)) is valof{
    logMsg(info,"char: $X");
    valis ContinueWith(cons(X,st))
  };
  
  concat(sequence of {},X) is X;
  concat(sequence of {H;..T},X) is sequence of {H;..concat(T,X)};
  
  main() do {
    SS is "a string";
    
    R is __string_iter(SS,shChar,ContinueWith(nil));
    
    logMsg(info,"R=$R");
    
    assert R=ContinueWith(cons of {'g'; 'n'; 'i'; 'r'; 't'; 's'; ' '; 'a'});
    
    TT is concat(SS," and more");
    assert TT="a string and more";
  }
}