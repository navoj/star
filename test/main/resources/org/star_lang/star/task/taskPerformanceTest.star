taskPerformanceTest is package {
  import task;

  prc spawnTest(numSpawn) do {
    
    -- work is taskBind(taskReturn(42), (v) => taskReturn(v));
    def work is taskReturn(42);
    
    once has type () => task of integer
    def once is () => valof {
      def completor is valof backgroundF(work);
      valis taskBind(completor, taskReturn);
    }
    
    def expected is 42 * numSpawn;
    
    def actual is let {
      loop has type (integer, integer) => task of integer
      fun loop(0, res) is taskReturn(res)
       |  loop(i, res) default is
            taskBind(once(), (v) => loop(i-1, res+v))

      -- "falling asleep" in the background is much more efficient than in the foreground...!
      def w is valof backgroundF(loop(numSpawn, 0));
      def  waitRes is w;
    } in
      executeTask(waitRes,raiser_fun);
          
    if actual != expected then
      logMsg(info, "$(actual) != $(expected)");
    assert(actual = expected);
  }

  prc benchmark(name, act,count) do {
    def st is nanos();
    act();
    def en is nanos();
    logMsg(info, "Benchmark $(name): $((en-st)/count) ns/task total $(((en-st) as float)/1000000000.0) s");
  }
  
  prc main() do {
    benchmark("spawn   10000", (() do spawnTest(10000)),10000l);
    benchmark("spawn  100000", (() do spawnTest(100000)),100000l);
    benchmark("spawn 1000000", (() do spawnTest(1000000)),1000000l);
  }
}
