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
mapLoopTest is package {
  comp(H1, H2) do {
    var counter := 0;
    
    for(K1->V1 in H1) do {
      logMsg(info, "Loop beginning");
      if (K1->V2 in H2) then {
        nothing;
      } else {
        counter := counter+1;
        logMsg(info, "Not present $K1");
      }
      logMsg(info, "Loop end: $counter");
      assert counter=1;
    }
  }

  main() do {
    H1 is map of {"A"->"0"};
    H2 is map of {"C"->"0";"D"->"1";"C"->"2"};
    comp(H1, H2);
  }
}