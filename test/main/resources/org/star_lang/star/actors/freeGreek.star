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
freeGreek is package{
  import actors;
  
  type GreekData is GreekData {
   	s has type string;
   	i has type integer;
  } implementing quotable
  
  
  handler is actor{
    Check has type action(GreekData);
    Check(data) do
      logMsg(info,"I was asked to check $data");
  };
  
  main() do {
    X is 10;
    var input:=GreekData{s="inString";i=1}
    for count in  iota(1,X,1) do {
   	  input:=GreekData{s="inString";i=count};
   	  request handler's Check  to Check(input);
	}
  }
}
    