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
-- test the list indexing functions
indextest is package{
  LL is list of ["a","b","c","d","e","f"];
  
  Ag is agg{
    LL = list of ["a","b","c","d","e","f"];
  };
  
  type Agg is agg{
    LL has type list of string;
  };
  
  CC is cons of ["a","b","c","d"];
  
  MM is map of {"a"->1;"b"->2;"c"->3};
  
  main has type action();
  main() do {
    logMsg(info,"$LL");
    XX is LL[size(LL)-1];
    assert XX="f";
    logMsg(info,"LL[\$]=$XX");
    
    EE is LL[size(LL)-2];
    logMsg(info,"EE = $EE");
    
    AA is LL[0];
    logMsg(info,"AA = $AA");
    assert AA="a";
        
    BB is Ag.LL[1];
    logMsg(info,"BB=$BB");
    
    DD is Ag.LL[size(LL)-3];
    logMsg(info,"DD=$DD");
    
    assert (LL[10] default "none") = "none";
    
    assert MM["a"]=1;
    assert MM["b"]=2;
    assert MM["c"]=3;
    assert (MM["e"] default nonInteger)=nonInteger;
    
    assert CC[0]="a";
    assert CC[1]="b";
  }
}