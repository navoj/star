notifyBlast is package{
  import actors;
  
  -- Test sending many notifies
  type pingType is alias of actor of {ping has type action(integer); count has type ref integer};
  
  pinger has type () => pingType;
  fun pinger() is actor{
    var count := 0;
    on X on ping do {
      -- logMsg(info,"Got $X");
      count := count+1;
    };
  };

  blast has type (integer,pingType)=>integer;
  fun blast(Count,A) is valof{
    var ix:=0;
    while ix<Count do{
      notify A with ix on ping;
      ix := ix+1
    };
    valis query A's count with count;
  }

  main has type ()=>()
  prc main() do {
    def Pi is pinger();
    
    def amnt is 50000000;
    
    var time := nanos();
    assert blast(amnt,Pi)=amnt;
    time := nanos()-time;
    
    logMsg(info,"blast of $(amnt) in $(time as float/1.0e9) seconds, $((amnt as long)*1000000000L/time) notify/sec, $(time/amnt as long) nanos/notify"); 
  }
}