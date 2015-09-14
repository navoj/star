picoRequest is package{
  
  -- test requests with and without write back 
  listener has type actor of{
    msg has type occurrence of ((string,integer));
    report has type ()=>list of ((string,integer));
    clearHistory has type action();
  }
  def listener is actor{
    on Ev on msg do append(Ev);
    
    fun report() is txs;
    
    prc clearHistory() do clear();
  } using {
    var txs := list of [];
    prc append(Ev) do extend txs with Ev;
    prc clear() do txs := list of [];
  };
  
  def events is list of {all ("event number $Ix",Ix) where Ix in range(1,10,1)}; 
 
  prc showHistory() do
  {
    def H is query listener's report with report();
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
      def listenerHistory is query listener's report with report();
    
      assert listenerHistory=events;
    }
  }
}
     