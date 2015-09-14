stocker is package{
  import actors;
  
  type eventTime is alias of long;
  
  stocker has type ()=>actor of{
    tick has type occurrence of ((float,eventTime));
    avePrice has type (eventTime,eventTime)=>float;
  };
  fun stocker() is actor{
    on (Price,When) on tick do
      extend prices with (Price,When);

    fun avePrice(Frm,To) is average(all Pr where (Pr,W) in prices and Frm=<W and W<To);
  } using {
    prices has type ref list of ((float,eventTime));
    var prices := list of [];
  };
  
  fun average(La) is sum(La)/size(La) as float using {
    fun sum(L) is valof{
      var total := 0.0;
      for P in L do
        total := total+P;
      valis total;
    }
  } 
  
  prc main() do {
    def S is stocker();
    
    for Pr in list of [ (10.0, 0L), (10.2,2L), (9.3,4L), (9.5,5L), (9.7,6L), (10.0,8L), (12.2,10L) ] do
      notify S with Pr on tick;
    logMsg(info,"price average 2-10: $(query S's avePrice with avePrice(2L,10L))");
  }
}
  