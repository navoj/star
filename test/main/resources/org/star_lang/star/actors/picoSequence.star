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
picoSequence is package{
  
  listener has type actor of {
    msg has type occurrence of ((string,integer));
    history has type ()=>list of ((string,integer));
    clearHistory has type action();
  }
  def listener is actor{
    on Ev on msg do
      append(Ev);
    
    fun history() is txs;
    prc clearHistory() do clear();
  } using {
    var txs := list of [];
    
    prc append(Ev) do extend txs with Ev;
    prc clear() do txs :=list of [];
  };
  
  def events is list of {all ("event number $Ix",Ix) where Ix in range(1,10,1)}; 
 
  prc showHistory() do {
    def H is query listener's history with history();
    logMsg(info,"history has $(size(H)) elements");
  } 
  
  prc main() do {
    for count in iota(0,100,1) do{
      request listener's clearHistory to clearHistory();
      
      for Ev in events do{
        notify listener with Ev on msg;
      };
    
      -- sleep(100L);
    
      showHistory();
      def listenerHistory is query listener's history with history();
    
      assert listenerHistory=events;
    }
  }
  
  fun select(P,L) is all e where (_,P(e)) in L;
}
     