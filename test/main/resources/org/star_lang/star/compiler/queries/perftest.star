perftest is package{
  def Size is 10000;
  
  def Base is indexed of {all ("$Ix",Ix) where Ix in (iota(1,Size,1) has type array of integer)};
  
  var ind := indexed{};
  
  {
    merge ind with Base;
  }
  
  prc main() do {
    -- logMsg(info,"base is $Base");
    
    def startBase is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in Base; 
      -- logMsg(info,"$XX");
    }
    def baseTime is nanos()-startBase;
    logMsg(info,"base time is $(baseTime/1000000L) milli-seconds");
    
    -- make sure index is seeded
    def firstInd is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in ind; 
      -- logMsg(info,"$Cx->$XX");
    }
    def firstIndTime is nanos()-firstInd;
    logMsg(info,"first indexed time is $(firstIndTime/100000L) milli-seconds");
    
    def startInd is nanos();
    for Cx in iota(1,Size,3) do
    {
      def XX is all Ix where (Ix,Cx) in ind; 
      -- logMsg(info,"$Cx->$XX");
    }
    def indTime is nanos()-startInd;
    logMsg(info,"indexed time is $(indTime/100000L) milli-seconds");
        
    -- logMsg(info,"indexed is $ind");
  }
}
  