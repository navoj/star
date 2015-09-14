queryforms is package{
  -- test out all the query forms
  
  def R is list of [(1,"alpha"), (2,"beta"), (3,"gamma"), (4,"delta"), (10,"alpha")];
  
  prc main() do {
    -- varying quantifier terms
    def Q0 is list of { all X where (1,X) in R}
    logMsg(info,"Q0=$Q0");
    assert Q0=list of ["alpha"];
    
    def Q1 is any of X where (1,X) in R;
    logMsg(info,"Q1=$Q1");
    assert Q1 has value "alpha";
    
    def Q2 is list of { unique X where (_,X) in R};
    logMsg(info,"Q2=$Q2");
    assert Q2 complement (list of ["alpha", "beta", "gamma", "delta"]) = list of [];
    
    def Q3 is 3 of X where (_,X) in R;
    logMsg(info,"Q3=$Q3");
    assert size(Q3)=3;
    
    def Q4 is list of { unique 1 of X where (_, X) in R};
    logMsg(info,"Q4=$Q4");
    assert Q4=list of ["alpha"];
    
    -- varying sorting constraints
    def S0 is list of { all X where (X,Y) in R order by X }
    logMsg(info,"S0=$S0");
    assert S0=list of [1,2,3,4,10];
    
    def S1 is list of { all X where (X,Y) in R order descending by Y }
    logMsg(info,"S1=$S1");
    assert S1=list of [3,4,2,1,10];
    
    def S2 is list of { all X where (X,Y) in R order by Y using (>) }
    logMsg(info,"S2=$S2");
    assert S2=list of [3,4,2,1,10];
    
    -- use the type of ... notation
    
    def T0 is list of {all X where (1,X) in R};
    logMsg(info,"T0=$T0");
    assert T0=list of ["alpha"];
    
    def T1 is cons of {all X where (1,X) in R};
    logMsg(info,"T1=$T1");
    assert T1=cons of ["alpha"];
    
    T2 has type list of string;
    def T2 is sequence of {all X where (1,X) in R};
    logMsg(info,"T2=$T2");
    assert T2= list of ["alpha"]
  }
}
    
    