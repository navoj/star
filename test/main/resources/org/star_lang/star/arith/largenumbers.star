largenumbers is package{
  plus has type (decimal,long) =>decimal
  fun plus(X,Y) is X+(Y as decimal)
  
  def A is 1a;
  
  sL has type long;
  def sL is smallest;
  
  lL has type long;
  def lL is largest;
  
  sF has type float;
  def sF is smallest;
  
  lF has type float
  def lF is largest;
  
  lD has type date;
  def lD is largest;
  
  main has type action();
  prc main() do {
    logMsg(info,"a large now $(plus(A,long(_now())))");
    
    logMsg(info, "smallest long $sL");
    logMsg(info, "largest long $lL");
    
    logMsg(info, "smallest float $sF");
    logMsg(info, "largest float $lF");
    
    logMsg(info,"end of time is $lD");
  };
};