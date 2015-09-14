-- A simple test of fluent relations
fluents is package{

  var fl := fluent{("alpha",1) at 1; ("beta",2) at 1~~4; ("gamma",3) at ~~4; ("beta",4) at 1};
  
  main has type action();
  main() do {
    logMsg(info,"Fluent fl is $fl");
    
    -- Look for ("beta",_) at 2~~3
    assert ("beta",_) at 2~~3 in fl;
    
    -- look for current values
    for current T in fl do
      logMsg(info,"current $T");
    
    -- look for current values that are one or after one
    for T at 1 in fl do
      logMsg(info,"$T at 1");
      
    -- add a bunch of entries
    for ix in iota(1,100,1) do
      initiate ("$ix",ix) at ix~~(ix+10) in fl;
    
    logMsg(info,"fl = $fl");
    
    -- forget some
    forget fl before 40;
    
    for ix in iota(1,120,1) do
      logMsg(info,"$(all T where T at ix in fl)");
  }
}
    
    