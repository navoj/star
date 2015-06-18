worksheet{

  type messageQ of a is alias of {
    post has type (a)=>task of ()
    poll has type ()=>task of a;
  }
  
  msgQ has type for all a such that ()=>messageQ of a;
  fun msgQ() is let{
    def postMsgChnl is channel();
    def grabMsgChnl is channel();

    fun qLoop(Q) where isEmpty(Q) is wait for postM(Q)
     |  qLoop(Q) default is wait for postM(Q) or pollM(Q)
 
    fun postM(Q) is 
      wrapRv(recvRv(postMsgChnl), (A) => qLoop([Q..,A]))

    fun pollM([A,..Q]) is let{
      fun reply(R) is valof{
        perform send(R,A);    -- reply on the one-time channel
        valis qLoop(Q)
      }
    } in wrapRv(recvRv(grabMsgChnl), reply);
    
    { ignore background qLoop(queue of []); }
  } in {
    fun post(A) is send(postMsgChnl,A)
    fun poll() is task{
      def ReplyChnl is channel();
      perform wait for sendRv(grabMsgChnl,ReplyChnl);
      valis valof (wait for recvRv(ReplyChnl))
    } 
  }
  
  fun sender(Q) is task{
    for i in range(0,1000,1) do 
      perform Q.post(i);
    sleep(3000l);
    logMsg(info,"all done");
  };
  
  fun worker(W,Q) is task{
    while true do{
      def Nx is valof Q.poll();
      logMsg(info,"$W doing $Nx");
      sleep(random(100L))
    }
  }
  
  def MQ is msgQ();
  ignore background worker("alpha",MQ);
  ignore background worker("beta",MQ);
  
  ignore valof sender(MQ);
}
