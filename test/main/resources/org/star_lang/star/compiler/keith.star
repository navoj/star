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
keith is package{
  -- test out some of the basic features of starrules

--  age has type list of ((string,integer));
  age is list of [
    ("tom",20),
    ("amy",19),
    ("steven",12),
    ("freya",19),
    ("olive",6),
    ("jessica",1)
  ];
  
  gender is list of[
    ("tom","male"),
    ("amy","female"),
    ("steven","male"),
    ("freya","female"),
    ("olive","female"),
    ("jessica","female")
  ];
  
  adultMales is all N where (N,A) in age and not (N,"female") in gender and A >=18;
  childFemales is all N where (N,A) in age and (N,"female") in gender and A < 18;
 
  main has type action();
  main() do {
    logMsg(info,"adultMales is $adultMales");
    logMsg(info,"childFemales is $childFemales");
  };
}