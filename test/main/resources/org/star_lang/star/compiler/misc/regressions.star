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
regressions is package{
  Ws(Set) is let{
    M is size(Set) as float;
    w1 is (M*sumXY(Set)-sumX(Set)*sumY(Set))/(M*sumX2(Set)-sq(sumX(Set)));
    w0 is sumY(Set)/M - (w1/M)*sumX(Set);
  } in (w1,w0);
    
  sq(X) is X*X;
  
  sumXY(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do{
      Total := Total+X*Y;
    }
    valis Total;
  };
  
  sumX(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+X;
    valis Total;
  }
  
  sumY(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+Y;
    valis Total;
  }
  
  sumX2(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+X*X;
    valis Total;
  }
  
  main() do {
    Set is list of [(1.0,2.0), (3.0,5.2), (4.0,6.8), (5.0,8.4), (9.0,14.8)];
    (W1,W0) is Ws(Set);
    logMsg(info,"w1=$W1,w0=$W0")
  }
}