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
doubler is package{
  -- This tests higher functions doubling themselves

  dbl has type ((%t) =>%t) => ((%t) =>%t);
  
  dbl(F) is let{
    -- ff has type (%t) =>%t;
    ff(X) is F(F(X));
  } in ff;
  
  compose has type ((%t)=>%u,(%s)=>%t)=>((%s)=>%u);
  compose(F,G) is (function(X) is F(G(X)));

  add has type (integer) => ((integer) =>integer);
  add(I) is let{
    a has type (integer) =>integer;
    a(X) is X+I;
  } in a;
  
  mul has type (integer) => ((integer)=>integer);
  mul(I) is let{
    m has type (integer) =>integer;
    m(X) is X*I;
  } in m;
  
  pls has type (integer) => ((integer) =>integer);
  pls(I) is (function(X) is X+I);
  
  tms has type (integer) => ((integer)=>integer);
  tms(I) is (function(X) is X*I);

  main has type action();
  main() do {
    -- first a simple test of add
    logMsg(info, "add(3)(4) is $(add(3)(4))");
    -- now we test double
    logMsg(info, "dbl(add(3))(4) is $(dbl(add(3))(4))");
    
    logMsg(info,"compose((mul(2),add(3))(5) = $(compose(mul(2),add(3))(5))");
  };
}