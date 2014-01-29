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
import volfunquery;
import ports;

queryfuntest is package{

  R1 is p0rt{
    testFun1 has type(string) => integer;
    testFun1(S) is size(S);
  }
    
  R2 is p0rt{
    testFun2 has type(string) => string;
    testFun2(S) is S++S;
  }
  
  Or is connectOr(R1,R2);
  
  main() do {
    A is query Or's testFun1 'n testFun2 with testFun1(testFun2("fred"));
    logMsg(info,"A=$A");
    assert A=8;
  }
}