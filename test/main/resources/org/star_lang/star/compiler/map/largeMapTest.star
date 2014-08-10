largeMapTest is package{

  type counter is counter{
    count has type ref integer;
  }
  
  limit is 1000000;
  
  main() do {
    var M := dictionary of {};
    
    start is nanos();
    for Ix in range(0,limit,1) do{
      Cx is random(limit);
      if M[Cx] matches R then
        R.count := R.count+1
      else{
        count is counter{count := 1;}
        M[Cx] := count;
      }
    }
    amnt is nanos()-start;
    logMsg(info,"Took $(amnt as float/1.0e9) seconds to do $limit updates, map has $(size(M)) elements")
  }
}
        