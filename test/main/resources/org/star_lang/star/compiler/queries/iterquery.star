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
iterquery is package{  
  S is list of {("alpha",1); ("beta",2); ("gamma",3) };
  T is list of {("aleph",1); ("delta",5); ("eta",7)};
  O is relation of {1; 3; 5};
  
  Size is 20000;
  
  In is all ("$Ix",Ix) where Ix in (iota(1,Size,1) has type list of integer);
  
  Od has type list of integer;
  Od is iota(1,Size,2);
    
  sho(M,X) is valof{
    logMsg(info,M);
    valis X;
  };
    
  main() do {
    logMsg(info,"O=$O");
    XX is all X where ("beta",X) in S;
    logMsg(info,"$XX");
    assert XX=array of {2};
    
    YY is 3 of X where ("alpha",X) in S;
    logMsg(info,"$YY");
    assert YY=array of{1};
    
    ZZ is relation of {all N where (N,X) in S and X in O};
    logMsg(info,"$ZZ");
    assert ZZ=relation of{"alpha";"gamma"};
    
    UU is all N where (N,X) in S and not X in O;
    logMsg(info,"UU=$UU");
    assert UU = array of{"beta"};

    VV is all N where o in O and ((N,o) in S otherwise (N,o) in T);
    logMsg(info,"$VV");

    start is nanos();
    EE is all N where (N,o) in In and not o in Od;
    time is nanos()-start;
    logMsg(info,"query took $((time as float)/1.0e9) seconds"); 
  }
}
