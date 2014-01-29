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
relForLoops is package{
  -- focus on for loops across relations
  
  R is relation{ (1,"one","alpha");
                 (2,"two","beta");
                 (3,"three","gamma");
                 (4,"four","delta");
                 (5,"five","eta")};
                 
  main() do {
    logMsg(info,"R=$R");
    
    for E in R do{
      logMsg(info,"E=$E");
    };
    
    var count := 0;
    for (3,N,L) in R do{
      logMsg(info,"N=$N, L=$L");
      count := count+1;
    }
    assert count=1;
  }
}
  