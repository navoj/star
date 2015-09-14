patternVars is package{

  type event is event{
    str has type string;
  };
  
  type possible of %t is possible(%t) or impossible;

  actorFun has type (string)=> actor{ 
    events has type occurrence of event;
  };
  
  actorFun(St) is actor{
    on Evt matching event{str=St} on events do
      logMsg(info,"We have $Evt");
    on event{str=S} on events do 
      logMsg(info,"event $S");
  }
  
  mapper(St) where St->X in wordMap is possible(X);
  mapper(_) default is impossible;
  
  wordMap is dictionary of [
    "hello" -> "Ok"
  ];
  
  tester(Str) is valof{
    if mapper(Str) matches possible(X) and X!="" then{
      logMsg(info,"We have a match");
      valis "$X!";
    }
    else
      valis "$Str??"
  }
  
  main() do {
    A is actorFun("hello");
    
    notify A with event{str="hello"} on events;
    notify A with event{str="again"} on events;
    
    notify A with event{str=tester("hello")} on events;
    notify A with event{str=tester("huh")} on events;
  }
    
}