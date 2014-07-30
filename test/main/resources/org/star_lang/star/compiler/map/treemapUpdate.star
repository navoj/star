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
treemapUpdate is package {
  private import dictionary

  main () do {
    tm0 is trEmpty["b"->3]
    logMsg(info,"tm0=$(__display(tm0))");

    tm1 is tm0["t"->3]["m"->3]
    logMsg(info,"tm1=$(__display(tm1))");

    tm2 is tm0["b"->5]
    logMsg(info,"tm2=$(__display(tm2))");
    tm3 is tm1["b"->5]
    logMsg(info,"tm3=$(__display(tm3))");

    logMsg(info, "tm0[b] = $(tm0["b"])");
    logMsg(info, "tm1[b] = $(tm1["b"])");
    logMsg(info, "--------")
    logMsg(info, "tm2[b] = $(tm2["b"])");
    logMsg(info, "tm3[b] = $(tm3["b"])"); 
    
    assert tm0["b"] = 3;
    assert tm1["b"] = 3;
    assert tm2["b"] = 5;
    assert tm3["b"] = 5;
  }
}