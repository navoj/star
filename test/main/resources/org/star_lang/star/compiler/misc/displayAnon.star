displayAnon is package{
  RR has type list of {name has type string; age has type integer}; 
  
  def RR is list of [ {name="john"; age=23}, {name="peter"; age=34}];
  
  prc main() do {
    def R is {name="john"; age=23};
    logMsg(info,"$R");
    logMsg(info,"$RR");
    
    def T is ("john",23);
    logMsg(info,"$T");
  }
}