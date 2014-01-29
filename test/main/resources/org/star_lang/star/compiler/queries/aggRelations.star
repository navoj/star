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
aggRelations is package{
  -- test out some of the aggregate relation stuff
  
  type testType is item{
    pos has type integer;
  } or empty;

  testf has type (relation of testType, integer) => relation of testType;
  testf(Is, P) is relation of { all X where (X matching item{pos=P}) in Is};
	
  main has type action();
  main() do {
    var ns := relation of {1;6;5;3;8;5;7;3;5;4};
	items is relation of {all item{pos=E} where E in ns};
	logMsg(info,"items = $items");
	
	logMsg(info, display(testf(items,3)));
  };
}