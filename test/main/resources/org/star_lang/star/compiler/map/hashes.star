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
hashes is package{  
  -- trial data
  
  M is dictionary of {("a",1); ("b",2); ("c",3)};
  
  main() do {
    logMsg(info,"M=$M");
    -- test indexable
    assert M["a"]=1;
    assert M["b"]=2;
    assert M["c"]=3;
    
    assert (M["d"] default nonInteger) = nonInteger;
    
    -- test sizeable
    assert size(M)=3;
    assert not isEmpty(M);
    
    -- test pPrint
    logMsg(info,"Showing M: $M");
    
    -- test iterate
    FF is _iterate(M,(function(V,ContinueWith(SS)) is ContinueWith(V+SS)),ContinueWith(0));
    assert FF=ContinueWith(6);
  }
}