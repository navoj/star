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
strindex is package{
  main() do {
    src is "the quick brown fox jumped over the lazy dog";
    
    assert src[0]='t';
    
    var dog := src;
    
    assert dog[4] = 'q';
    dog[4] := '%';
    assert dog[4]='%';
    
    logMsg(info,"$dog");
    
    assert src[4:9]="quick";
    
    dog[4:findstring(dog," jumped",0)] := "cat";
    
    logMsg(info,"$dog");
    assert dog="the cat jumped over the lazy dog";
  }
}