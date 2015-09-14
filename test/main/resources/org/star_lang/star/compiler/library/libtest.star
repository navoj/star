libtest is package{
  import libmanifest;
  import persons;
  
  prc main() do {
    logMsg(info,"people are $people");
    assert size(people)=5;
    def S is msort(people);
    logMsg(info,"sorted people $S");
    
    assert ordered(S);
  }
}
