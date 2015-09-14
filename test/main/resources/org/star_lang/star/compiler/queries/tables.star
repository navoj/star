tables is package{
  -- test out some of the basic tables and relations stuff

  R has type list of ((string,integer));
  def R is list of[
    ("a",1),
    ("b",2),
    ("c",3),
    ("a",4)
  ];
  
  P has type list of {name has type string;age has type integer};
  def P is list of [ {name="fred";age=24}, {name="peter";age=23}];
  
  check has type (string,list of ((string,integer))) =>boolean;
  fun check(S,Rr) is ((SS,_) where SS=S) in Rr;
    
  main has type action();
  prc main() do {
    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");

    assert check("a",R);
    assert not check("e",R);
    
    logMsg(info,"is fred in P? $(all pp where pp in P and pp.name="fred")");

    assert (all pp where pp in P and pp.name="fred")=list of [{name="fred";age=24}];
    assert (all pp where pp in P and pp.name="peter")!=list of [{name="fred";age=24}];
  }
}