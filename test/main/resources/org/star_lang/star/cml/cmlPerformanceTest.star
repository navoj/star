/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
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
    task1(loops, v) default is taskBind(throwit(v+1), (function(_) is taskBind(catchit, (function(v2) is task1(loops-1, v2)))))
    
    -- catch-throw
    task2 has type (integer) => task of integer
    task2(0) is taskReturn(42)
    task2(loops) default is taskBind(catchit, (function(v) is taskBind(throwit(v+1), (function(_) is task2(loops-1)))))
    
    -- start both in background and wait for them
    start1 is backgroundF(task1(nloops, 0))
    start2 is backgroundF(task2(nloops))
    op is taskBind(start1, (function (compl1) is
       taskBind(start2, (function (compl2) is
         taskBind(compl1, (function (r1) is
         taskBind(compl2, (function (r2) is taskReturn((2*nloops + 42) = (r1+r2))))))))));
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
