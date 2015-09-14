mergerelation is package{
        
  var Scores := list of [
    {name="j"; amount=1},
    {name="p"; amount=2},
    {name="m"; amount=0}
  ];      
  prc main() do {
    logMsg(info, "Test the merge relation function");
    merge Scores with list of [{name="X"; amount=9}];
    
    assert size(Scores)=4;
    assert {name="X"} in Scores;
    assert {name="j";amount=1} in Scores;
    assert {name="p";amount=M} in Scores and M=<2 and M>1;
    assert {name="m";amount=0} in Scores;
    assert not {name="j";amount=2} in Scores;
    
    logMsg(info,"Scores is $Scores");
  }
}