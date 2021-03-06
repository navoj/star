cmlPerformanceTest is package {
  import task;
  import cml;

  task_ping_pong_(nops) is let {
    nloops is nops / 2;
    
    ch is channel()
        
    throwit(v) is send(ch, v)
    catchit is recv(ch)
    
    -- throw-catch
    task1 has type (integer, integer) => task of integer
    task1(0, v) is taskReturn(v)
    task1(loops, v) default is taskBind(throwit(v+1), (_) => taskBind(catchit, (v2) => task1(loops-1, v2)))
    
    -- catch-throw
    task2 has type (integer) => task of integer
    task2(0) is taskReturn(42)
    task2(loops) default is taskBind(catchit, (v) => taskBind(throwit(v+1), (_) => task2(loops-1)))
    
    -- start both in background and wait for them
    start1 is backgroundF(task1(nloops, 0))
    start2 is backgroundF(task2(nloops))
    op is taskBind(start1, (compl1) =>
       taskBind(start2, (compl2) =>
         taskBind(compl1, (r1) =>
         taskBind(compl2, (r2) => taskReturn((2*nloops + 42) = (r1+r2))))));
  } in valof op;
  
  task_ping_pong(nops) do assert(task_ping_pong_(nops))

  benchmark(name, act, nops) do {
    st is nanos();
    act(nops);
    en is nanos();
    secs is ((en-st) as float)/1000000000.0;
    throughput is (nops as float)/secs;
    logMsg(info, "Benchmark $(name): $(secs) s => $(throughput) ops/s");
  }
  
  main() do {
    benchmark("task ping pong", task_ping_pong, 1000000);
  }
  
}
