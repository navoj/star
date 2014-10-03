/**
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
notifyBlast is package{
  import actors;
  
  -- Test sending many notifies
  type pingType is alias of actor of {ping has type action(integer); count has type ref integer};
  pinger has type ()=> pingType;
  pinger() is actor{
    var count := 0;
    on X on ping do {
      -- logMsg(info,"Got $X");
      count := count+1;
    };
  };

  blast has type (integer,pingType)=>integer;
  blast(Count,A) is valof{
    var ix:=0;
    while ix<Count do{
      notify A with ix on ping;
      ix := ix+1
    };
    valis query A's count with count;
  }

  main() do {
    Pi is pinger();
    
    amnt is 50000000;
    
    var time := nanos();
    assert blast(amnt,Pi)=amnt;
    time := nanos()-time;
    
    logMsg(info,"blast of $(amnt) in $(time as float/1.0e9) seconds, $((amnt as long)*1000000000L/time) notify/sec, $(time/amnt as long) nanos/notify"); 
  }
}