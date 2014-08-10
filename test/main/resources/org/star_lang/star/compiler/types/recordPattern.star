recordPattern is package{
  R is list of [ ("alpha",1), ("beta", 2) ];
  
  run(F) do {
    RR is F({REL1=R;KK=23});
    
    logMsg(info,__display(RR));
    
    assert size(RR)=size(R);
  } 
  
  main() do {
    run((function({REL1 = (REL1 has type list of ((string,integer)))}) is (all x where x in REL1) ));
  }
} 