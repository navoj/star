worksheet{
  fun sieve(inChannel,Max) is valof {
    def nxPrime is valof (wait for recvRv(inChannel));
    -- logMsg(info,"next prime is $nxPrime");
    if nxPrime<Max then
      valis sieve(filter(nxPrime,inChannel),Max)
    else
      valis nxPrime
  };
  
  def naturals is let {
    def natChannel is channel();
    { logMsg(info,"starting...");
      ignore background task {
        var counter := 3;
        while true do{
         -- logMsg(info,"sending... $counter on $natChannel");
          perform send(natChannel,counter);
          counter := counter+2;
        }
      }
    }
  } in natChannel;
  
  fun filter(P,inChannel) is let{
    def outChannel is channel();
    fun loop() is task{
      while true do {
        def I is valof (wait for recvRv(inChannel));
        if I%P!=0 then -- not a multiple, pass it on
          perform send(outChannel,I)
      }
    };

    { ignore background loop() }
  } in outChannel;
    
  assert sieve(naturals,10000)=10007
}  
