mapActors is package{

  type msg is msg{
    details has type dictionary of (string,msgContent);
    count has type integer;
  }
  
  type msgContent is content{
    data has type string
  }
  
  recep has type (string)=>actor of {
    dataChannel has type occurrence of any;
  }
  fun recep(Key) is actor{
    on Data on dataChannel do
      logMsg(info,"Actor $Key got $Data");
  }
  
  def portActor is actor{
    on msg{count=Cx;details=Body} on msgChannel do{
  	  for K->content{data=D} in Body do {
  	    send(K,D)
  	  } using{
        prc send("flight",F) do notify Flights with (F cast any) on dataChannel
         |  send("rec",R) do notify Rec with (R cast any) on dataChannel
         |  send(KK,M) default do  logMsg(info,"Funny input: $K/$M");
      }
  	}
  } 
  
  def Flights is recep("flightAgent");
  def Rec is recep("receiverAgent");
  
  prc main() do {
    notify portActor with msg{count=4; details=dictionary of ["flight"->content{data="UA838"}, "rec"->content{data="24Tons"}, "??"->content{data="GIGO"}]} on msgChannel
  }
}