worksheet{
  import concurrency;
  
  listen(integer(P)) is __tcp_listen(P);
  
  server(P) is task{
    L is listen(P);
    
    while true do {
      C is __accept(L,acceptor);
      logMsg(info,"new connection on $C");
    }
  }
  
  acceptor(success(C)) do consumeInput(0,C);
    
  consumeInput(Start,F) do let{
    consume(success((_,""))) do logMsg(info,"end of input");
    consume(success((Cnt,S))) do {
      logMsg(info,"next block of $Cnt bytes, #(__display(S))");
      consumeInput(Start+(Cnt as long),F);
    }
  } in { __asynch_read(F,Start,consume) };
  
  ignore background server(9090);
  
  T is __tcp_connect("localhost",9090,sendData);
  
  show T;
  
  sendData(success(C)) do __async_write(C,0,