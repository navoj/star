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
taskTest is package {
  import task;
  
  testBasics() do {
    t1 has type task of integer;
    t1 is taskReturn(42);
    assert(executeTask(t1,raiser_fun) = 42);
    
    t2 is taskBind(taskReturn(2), (function (v) is taskReturn(21 * v)));
    assert(executeTask(t2,raiser_fun) = 42);
  }

  testWait1() do {
    t is taskWait((procedure (wakeup) do wakeup(taskReturn(42))));
    v is executeTask(t,raiser_fun);
    assert(v = 42);
  }

  testWait2() do {
    var ctrl := 0;
    t is taskWait((procedure (wakeup) do {
      _ is __spawnExp((function () is valof {
        sync(ref ctrl) {
          when ctrl = 0 do ctrl := 1;
        }
        wakeup(taskReturn(42));
        valis 0;
      }));
    }));
    sync(ref ctrl) {
      assert(ctrl = 0); -- thread not started yet
    }
    v is executeTask(t,raiser_fun); -- now thread will start, we wait for wakeup
    sync(ref ctrl) {
      assert(ctrl = 1);
    }
    assert(v = 42);
  }
  
  
  testExn1() do {
    err is taskFail(exception("","Something went wrong" cast any,__location__))
    var _failed := false;
    try {
      r is executeTask(err,raiser_fun);
    } catch {
      _failed := true;
    }
    assert(_failed);
  }
  
  testExn2() do {
    err is taskFail(exception(nonString,"Something went wrong" cast any, __location__))
    var _failed := false;
    try {
      var _t_failed := false;
      c is taskCatch(err, (function (e) is
        valof { -- logMsg(info, e); 
                _t_failed := true; valis taskReturn(0); }));
      
      r is executeTask(c,raiser_fun);
      assert(_t_failed);
      assert(r = 0);
    } catch {
      _failed := true;
    }
    assert(not _failed);
  }
  
  testUtils() do {
    t1 is taskLift((function () is 42));
    assert(executeTask(t1,raiser_fun) = 42);
    
    t2 is taskGuard((function () is taskReturn(42)));
    assert(executeTask(t2,raiser_fun) = 42);
  } 

  main() do {
    testBasics();
    testWait1();
    testWait2();
    testExn1();
    testExn2();
    
    testUtils();
    logMsg(info, "taskTest done");
  }
  
}
