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
funnyTestable is package {

  contract Testable over %s is {
    toTest has type for all %t such that (%s,%t) => integer where sizeable over %t;
  };

  implementation Testable over string is {
      toTest = (function (a,b) is (size(a)+size(b)));    
  };

  main() do { 

    t1 is toTest("Hello",list of [1,2,3]);
    logMsg(info, "t1 is $t1"); 

    -- the next line got a type error saying 
    -- "dictionary literal not valid here 
    -- because list of integer not equal to map of(%__47213, %__47214)" 

    t2 is toTest("Good Morning",dictionary of {1->1;2->2}); -- now commented out to pass type checking
    logMsg(info, "t2 is $t2");
  }
}