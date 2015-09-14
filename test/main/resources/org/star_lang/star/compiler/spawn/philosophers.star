philoshopers is package{
  import semaphore;
    
  -- Dining philosophers using concurrency primitives
  
  
  prc table(Count) do let{
    def T is semaphore(3);
      
    phil has type (integer, sem, sem)=>task of (());
    fun phil(n,L,R) is task{
        for Ix in range(0,Count,1) do{
          -- sleep(random(15L));
          perform T.grab();  -- get permission first
          perform L.grab();
          perform R.grab();
          -- logMsg(info,"Phil $n is eating for the $(Ix)th time");
          perform T.release();
          -- logMsg(info,"Table released");
          perform L.release();
          -- logMsg(info,"Left released");
          perform R.release();
          -- logMsg(info,"Right released");
        }
        logMsg(info,"Phil $n ate for $(Count) times");
      };
    } in {
      def fork1 is semaphore(1);
      def fork2 is semaphore(1);
      def fork3 is semaphore(1);
      def fork4 is semaphore(1);
    
      def phil1 is background phil(1,fork1,fork2);
      def phil2 is background phil(2,fork2,fork3);
      def phil3 is background phil(3,fork3,fork4);
      def phil4 is background phil(4,fork4,fork1);
    
      perform phil1;
      perform phil2;
      perform phil3;
      perform phil4;
    };
    
  prc main() do {
    table(10000);
  }
}
