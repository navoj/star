largenumbers is package{

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
    logMsg(info, "smallest long $sL");
    logMsg(info, "largest long $lL");
    
    logMsg(info, "smallest float $sF");
    logMsg(info, "largest float $lF");
    
    logMsg(info,"end of time is $lD");
  };
};