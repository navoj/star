whereActor is package{

  type flowActorType is alias of actor{
    inflow has type port of (integer,integer);
    outflow has type occurrence of (integer,integer);
  };
  
  posActor has type flowActorType; 
  posActor is actor{
    on (X where X>0) at T from inflow do
      logMsg(info,"Positive X: $X");
  };
  
  negActor has type flowActorType; 
  negActor is actor{
    on (X where X<0) at T from inflow do
      logMsg(info,"Negative X: $X");
  };
  
  main() do {
    var ix := 0;
    
    while ix<10000 do
    {
      post ix at ix to inflow of posActor;
      post -ix at ix to inflow of posActor;
      post ix at ix to inflow of negActor;
      post -ix at ix to inflow of negActor;
      ix := ix+1;
    };
    
    sleep(1000);
    logMsg(info,"all done");
  }
};