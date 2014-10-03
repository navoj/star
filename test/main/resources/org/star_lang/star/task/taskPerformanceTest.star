/**
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
taskPerformanceTest is package {
  import task;

  spawnTest(numSpawn) do {
    
    -- work is taskBind(taskReturn(42), (function(v) is taskReturn(v)));
    work is taskReturn(42);
    
    one has type () => task of integer
    one is (function () is valof {
      completor is valof backgroundF(work);
      valis taskBind(completor, taskReturn);
    })
             
    
    expected is 42 * numSpawn;
    actual is let {
      loop has type (integer, integer) => task of integer
      loop(0, res) is taskReturn(res)
      loop(i, res) default is
        taskBind(one(), (function (v) is loop(i-1, res+v)));

      -- "falling asleep" in the background is much more efficient than in the foreground...!
      w is valof backgroundF(loop(numSpawn, 0));
      waitRes is w;
    } in
      executeTask(waitRes,raiser_fun);
          
    if actual != expected then
      logMsg(info, "$(actual) != $(expected)");
    assert(actual = expected);
  }

  benchmark(name, act,count) do {
    st is nanos();
    act();
    en is nanos();
    logMsg(info, "Benchmark $(name): $((en-st)/count) ns/task total $(((en-st) as float)/1000000000.0) s");
  }
  
  main() do {
    benchmark("spawn   10000", (procedure () do spawnTest(10000)),10000l);
    benchmark("spawn  100000", (procedure () do spawnTest(100000)),100000l);
    benchmark("spawn 1000000", (procedure () do spawnTest(1000000)),1000000l);
  }
}
