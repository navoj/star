largeMapTest is package{

  type counter is counter{
    count has type ref integer;
  }
  
  def limit is 1000000;
  
  prc main() do {
    var M := dictionary of {};
    
    def start is nanos();
    for Ix in range(0,limit,1) do{
      def Cx is random(limit);
      if M[Cx] has value R then
        R.count := R.count+1
      else{
        def count is counter{count := 1;}
        M[Cx] := count;
      }
    }
    def amnt is nanos()-start;
    logMsg(info,"Took $(amnt as float/1.0e9) seconds to do $limit updates, map has $(size(M)) elements")
  }
}
        