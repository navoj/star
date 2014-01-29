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
whilePerf is package{

  unboxed() do {
    var myStr := ""_;
    var i := 0_;
    var startTime := smallest;
    var stopTime  := smallest;
    var keepLooping := true;
    	 
    while keepLooping do {
      if __integer_eq(i,100_) then {
		startTime := nanos();
	  }
	  if __integer_eq(i, 999999_) then {
		stopTime := nanos();
		timeToProcess is (stopTime - startTime) / 1000000L;
		logMsg(info,"Process time: $timeToProcess milliseconds");
		keepLooping := false;
	  }			
	  myStr := __string_concatenate("Test"_,__integer_string(i));
	  i := __integer_plus(i,1_);						
    }
  }
	
  boxed() do {
    var myStr := "";
    var i := 0;
    var startTime := smallest;
    var stopTime  := smallest;
    var keepLooping := true;
    	 
    while keepLooping do {
      if i = 100 then {
		startTime := nanos();
	  }
	  if i = 999999 then {
		stopTime := nanos();
		timeToProcess is (stopTime - startTime) / 1000000L;
		logMsg(info,"Process time: $timeToProcess milliseconds");
		keepLooping := false;
	  }			
	  myStr := "Test$i";
	  i := i + 1;						
    }
  }
  
  main() do {
    boxed();
    unboxed();
  }
};