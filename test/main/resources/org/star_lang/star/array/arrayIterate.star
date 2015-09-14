arrayIterate is package{
  II has type list of integer;
  def II is iota(100,1,-1);
  
  prc main() do {
    def XX is all I where I in II;
    logMsg(info,"$XX");
    def YY is all (I,Ix) where (Ix->I) in II;
    logMsg(info,"$YY");
  }
}