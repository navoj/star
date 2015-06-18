recordPattern is package{
  def R is list of [ ("alpha",1), ("beta", 2) ];
  
  prc run(F) do {
    def RR is F({REL1=R;KK=23});
    
    logMsg(info,__display(RR));
    
    assert size(RR)=size(R);
  } 
  
  prc main() do {
    run((({REL1 = (REL1 has type list of ((string,integer)))}) => (all x where x in REL1) ));
  }
} 