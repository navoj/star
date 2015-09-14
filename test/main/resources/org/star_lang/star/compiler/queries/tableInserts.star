tableInserts is package{
  -- test out adding elements to a list

  R has type ref list of ((string,integer));
  var R := list of [
    ("a",1)
  ];
  
  check has type (string,list of ((string,integer))) =>boolean;
  fun check(S,Rl) is ((SS,_) where SS=S) in Rl;
  
  prc main() do {
    assert not ("b",2) in R;
    extend R with ("b",2);
    assert ("b",2) in R;
    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");
    
    merge R with list of [("e",5)];
    logMsg(info,"is e in $R? $(check("e",R))");
    assert ("e",5) in R;
    
    merge R with list of [("aa",1), ("bb",2), ("cc",3)];
    logMsg(info,"is cc in $R? $(check("cc",R))");
    assert ("aa",_) in R;
    
    RR has type ref list of ((string,integer));
    var RR := list of [];
    for KV in (all (K,V) where (K,V) in R and V>1) do
      extend RR with KV;
    logMsg(info,"RR=$RR");
  }
}