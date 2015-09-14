ptest1 is package{
  
  def R is list of [("fred",1),("fred",3),("Bob",3)];
  def TM is ((X) from ("fred",X));
  
  prc main() do {
  	
  	if ("fred",3) in R then {
  		logMsg(info, "(\"fred\",3) is in R");  
 	 } else {
  		logMsg(info, "(\"fred\",3) is not in R");  
  	};
  	
  	if TM(3) in R then {
  		logMsg(info, "TM(3) is in R");  
 	 } else {
  		logMsg(info, "TM(3) is not in R");  
  	};
  }
}