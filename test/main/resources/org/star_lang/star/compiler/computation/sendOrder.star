sendOrder is package {
  import task;
  
  var RR := list of [];
  
  fun senderC(list of []) is task { valis () }
   |  senderC(list of [msg,..rest]) is task {
        logMsg(info, "sending $(__display(msg)); remaining: #(__display(rest))");
        RR := list of [RR..,msg];
        valis valof senderC(rest);
      };

  prc main() do {
    def _ is valof senderC(list of [1,2,3]);
    
    logMsg(info,"RR=$RR");
    assert RR = list of [1,2,3];
  }
}