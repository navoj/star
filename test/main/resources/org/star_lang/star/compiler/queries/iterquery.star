iterquery is package{
  def S is list of [("alpha",1), ("beta",2), ("gamma",3) ];
  def T is list of [("aleph",1), ("delta",5), ("eta",7)];
  def O is list of [1, 3, 5];
  
  def Size is 20000;
  
  def In is all ("$Ix",Ix) where Ix in (iota(1,Size,1) has type list of integer);
  
  Od has type list of integer;
  def Od is iota(1,Size,2);
    
  fun sho(M,X) is valof{
    logMsg(info,M);
    valis X;
  };
    
  prc main() do {
    logMsg(info,"O=$O");
    def XX is all X where ("beta",X) in S;
    logMsg(info,"$XX");
    assert XX=list of [2];
    
    def YY is 3 of X where ("alpha",X) in S;
    logMsg(info,"$YY");
    assert YY=list of [1];
    
    def ZZ is list of {all N where (N,X) in S and X in O};
    logMsg(info,"$ZZ");
    assert ZZ=list of ["alpha", "gamma"];
    
    def UU is all N where (N,X) in S and not X in O;
    logMsg(info,"UU=$UU");
    assert UU = list of ["beta"];

    def VV is all N where o in O and ((N,o) in S otherwise (N,o) in T);
    logMsg(info,"$VV");

    def start is nanos();
    def EE is all N where (N,o) in In and not o in Od;
    def time is nanos()-start;
    logMsg(info,"query took $((time as float)/1.0e9) seconds"); 
  }
}
