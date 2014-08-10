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
picoSequence is package{
  
  listener has type actor of {
    msg has type stream of ((string,integer));
    history has type ()=>list of ((string,integer));
    clearHistory has type action();
  }
  listener is actor{
    on Ev on msg do
      append(Ev);
    
    history() is txs;
    clearHistory() do clear();
  } using {
    var txs := list of [];
    
    append(Ev) do extend txs with Ev;
    clear() do txs :=list of [];
  };
  
  events is list of {all ("event number $Ix",Ix) where Ix in range(1,10,1)}; 
 
  showHistory() do {
    H is query listener's history with history();
    logMsg(info,"history has $(size(H)) elements");
  } 
  
  main() do {
    for count in iota(0,100,1) do{
      request listener's clearHistory to clearHistory();
      
      for Ev in events do{
        notify listener with Ev on msg;
      };
    
      -- sleep(100L);
    
      showHistory();
      listenerHistory is query listener's history with history();
    
      assert listenerHistory=events;
    }
  }
  
  select(P,L) is all e where (_,P(e)) in L;
}
     