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
cmlTest is package {
  
  import task;
  -- import cml;

  import cmlQueue;
  import cmlAtomicRef;
  
  testQueue1() do {
    q1 is mk_queue((e) => true);
    expected is "Hello";
    (actual, _) is dequeue(enqueue(q1, expected))
    assert (actual = some(expected));

    -- dequeueMatch    
    var q2 := mk_queue((e) => true);
    q2 := enqueue(q2, 1);
    q2 := enqueue(q2, 2);
    q2 := enqueue(q2, 3);
    q2 := enqueue(q2, 4);
    (actual2, _) is dequeue_match(q2, (e) => e = 2);
    
    (actual3, _) is dequeue(q2);
    assert(actual3 = some(1));
    assert(actual2 = some(2));
    -- assert(dequeue(q2) = some(3));
    -- assert(dequeue(q2) = some(4));
  }
  
  testAtomicRef() do {
    logMsg(info, "testAtomicRef() - Start");

    a1 is atomic_int(42_);
    a2 is a1;
    a3 is atomic_int(42_);

    -- referential equality
    assert(atomic_int_ref_eq(a1, a2));
    assert(not atomic_int_ref_eq(a1, a3));
    
    assert(__integer_eq(atomic_int_cas(a1, 23_, 0_), 42_)); -- swapping should fail
    assert(__integer_eq(atomic_int_cas(a1, 42_, 0_), 42_)); -- swapping should succeed
    
    check(a) do {
      if not atomic_int_ref_eq(a1, a) then
        logMsg(info, "check 1 failed.");
      if atomic_int_ref_eq(a3, a) then
        logMsg(info, "check 2 failed");
      -- assert(atomic_int_ref_eq(a1, a));
      -- assert(not atomic_int_ref_eq(a3, a));
    };
    
    _ is __spawnExp((function () is valof {
      -- logMsg(info, "testAtomicRef() - Child thread start; current ref: $(integer(__atomic_int_reference(a1)))");
      check(a1);
      var done := false;
      -- wait for a1 to become 1, then set it to 2
      while (not done) do {
        switch atomic_int_cas(a1, 1_, 2_) in {
          case 1_ do { logMsg(info, "thread loop done, cas 1->2 succeeded"); done := true; }
          case _ default do __yield(); -- loop
        }
      }
      logMsg(info, "testAtomicRef() - Child thread done; current ref: $(integer(__atomic_int_reference(a1)))");
      assert(__integer_gt(__atomic_int_reference(a1), 1_)); -- must be still 2 or 3 already
      valis 0;
    }));
    
    logMsg(info, "testAtomicRef() - let the child thread run;");
    var done2 := false;
    -- let thread "start"
    assert(__integer_eq(atomic_int_cas(a1, 0_, 1_), 0_));
    -- wait for a1 to become 2, then set it to 3
    while (not done2) do {
      switch atomic_int_cas(a1, 2_, 3_) in {
        case 2_ do done2 := true;
        case _ default do sleep(1000L); -- __yield(); -- loop
      }
    }
    assert(__integer_eq(__atomic_int_reference(a1), 3_));
    logMsg(info, "testAtomicRef() - Done");
  }

  import cml;

  -- basic rendezvous creation
  testCML_create() {
      logMsg(info, "testCML_create - Start");
	  ch1 is channel();
	  recv1 is recvRv(ch1);
	  send1 is sendRv(ch1, 1);
	  
	  recv2 is wrapRv(recv1, (v) => taskReturn(v + 1));
	  send2 is wrapRv(send1, (_) => taskReturn(()));
	  
	  choose1 is chooseRv(cons of { recv1; recv2 });

      -- nothing synced here
      logMsg(info, "$(__display(choose1))");
      logMsg(info, "testCML_create - End");
  }
  
  -- basic synchronization
  testCML_sync() {
    logMsg(info, "testCML_sync - Start");
    for i in iota(0, 2, 1) do {
      ch1 is channel()
    
      expected is 42
    
      _ is __spawnExp((function () is valof {
        if (i = 1) then sleep(100L);
        _ is executeTask(send(ch1, expected), raiser_fun);
        valis 0;
      }));
    
      if (i = 2) then sleep(100L);
      actual is executeTask(recv(ch1), raiser_fun);
    
      assert(actual = expected)
    }
    logMsg(info, "testCML_sync - End");
  }
  
  -- wrapping
  testCML_wrap() {
    logMsg(info, "testCML_wrap - Start");
    ch1 is channel()
    
    expected is 42
    
    _ is __spawnExp((function () is valof {
      -- sleep(100L);
      Rv is sendRv(ch1, expected*2)
      _ is executeTask(await(Rv), raiser_fun);
      valis 0;
    }));
    
    sleep(100L);
    getHalf is wrapRv(recvRv(ch1), (n) => taskReturn(n/2))
    
    actual is executeTask(await(getHalf), raiser_fun);
    
    assert(actual = expected)
    logMsg(info, "testCML_wrap - End");
  }

  -- Tests that wrappers are called on the 'receiving' side, not on the 'sending' side.
  testCML_wrap2() {
    logMsg(info, "testCML_wrap2 - Start");

    ch1 is channel();
    
    v1 is 20;
    v2 is 22;
    expected is v1+v2;
    
    _ is __spawnExp((function () is valof {
      -- we also need a delay here, so that the sending side will always (more likely) be the synchronizing side
      sleep(100L);
      -- send two times
      logMsg(info, "sending first value..");
      x is executeTaskOnThreadPool(taskBind(await(
        wrapRv(sendRv(ch1, v1),
             (_) => valof { logMsg(info, "first send successfull"); valis taskReturn(()); }))
        ,  (r) => valof {
            logMsg(info, "first send sync returned");
            valis taskReturn(r);
          })
      , raiser_fun);
      logMsg(info, "sending second value..");
      y is executeTaskOnThreadPool(send(ch1, v2), raiser_fun);
      logMsg(info, "helper done");
      valis 0;
    }));
      
    -- receive one, then receive the next immediately in the wrapper function.
    -- if the wrapper function is evaluated in the sending/synchronizing context,
    -- then the second recv will not happen, as no second value will be sent.
    getPrv is wrapRv(recvRv(ch1),
                    (n) => valof {
                     logMsg(info, "received first value $(n)");
                     logMsg(info, "=========================");
                     sleep(2000L); -- eval of wrap-fun seems to prevent the completion of the first send
                     receiveOr is (def) => chooseRv(cons of {
                        recvRv(ch1);
                        wrapRv(timeoutRv(1000L), (_) => taskReturn(def)); });
                      
                     next is executeTaskOnThreadPool(await(receiveOr(0)), raiser_fun);
                     if next = 0 then logMsg(info, "timeout!!!") else logMsg(info, "received second value $(next)");
                     valis taskReturn(n+next); -- the 0 will make the assert below fail
                     });
    
    logMsg(info, "starting receiver fiber");
    actual is executeTaskOnThreadPool(await(getPrv), raiser_fun);
    assert(actual = expected);
    logMsg(info, "testCML_wrap2 - End");
  }
  
  testCML_wrap2new() {
    logMsg(info, "testCML_wrap2new - Start");

    ch1 is channel();
    
    expected is 42;
    
    sendRv1 is sendRv(ch1, expected);
    recvRv1 is recvRv(ch1);
    recvOrTimeoutRv is chooseRv(cons of { recvRv1; wrapRv(timeoutRv(2000L), (_) => task { valis 0; }) });
    
    sending is task {
      -- we also need a delay here, so that the sending side will always (more likely) be the synchronizing side
      sleep(100L); -- TODO differently

      x is valof await(sendRv1);
      y is valof await(sendRv1);
      valis 0;
    }    
    receiving is task {
      n is valof await(recvRv1);
      next is valof await(recvOrTimeoutRv); -- will be 0 on timeout
      valis n + next;
    }
    
    actual is valof task {
      _ is valof backgroundF(sending);
      v is valof receiving;
      valis v;
    }
    assert(actual = (expected*2));
    logMsg(info, "testCML_wrap2new - End");
  }
  
  testCML_choose() {
    logMsg(info, "testCML_choose - Start");
    -- this is undeterministic: if we loop long enough it should work.
    loops is 124
    
    ch1 is channel()
    -- two threads sending over different channels
    -- the first one will be preferred by choosePrv, so we delay it
    _ is __spawnExp((function () is valof {
      -- sleep(100L);
      for i in iota(1, loops, 1) do {
        _ is executeTask(send(ch1, i), raiser_fun);
        sleep(1L);
      };
      valis 0;
    }));
    
    ch2 is channel()
    _ is __spawnExp((function () is valof {
      -- sleep(100L);
      for i in iota(1, loops, 1) do {
        _ is executeTask(send(ch2, -i), raiser_fun);
      };
      valis 0;
    }));
    
    -- if we receive some numbers, there should be at least one of each after a short time
    var seen1 := false
    var seen2 := false
    var seen_both_at := -1
    for i in iota(1, loops*2, 1) do {
      v is executeTask(await(chooseRv(cons of { recvRv(ch1);
                                              recvRv(ch2) })), raiser_fun);
      if (v > 0) then seen1 := true;
      if (v < 0) then seen2 := true;
      logMsg(info, "$(v)");
      if (seen1 and seen2 and (seen_both_at = -1)) then seen_both_at := i
    }
    assert(seen_both_at > -1);
    assert(seen_both_at < (loops/4)) -- "a short time" ??

    logMsg(info, "testCML_choose - End");
  }
  
  testCML_choose_never() {
    logMsg(info, "testCML_choose_never - Start");
    ch is channel()
    _ is __spawnExp((function () is valof {
      for i in iota(1, 1000, 1) do {
        _ is executeTask(send(ch, 42), raiser_fun);
      }
      valis 0;
    }));
    for i in iota(1, 1000, 1) do {
      v is executeTask(await(chooseRv(cons of { wrapRv(neverRv, (_) => taskReturn(666));
                                              recvRv(ch) })), raiser_fun);
      assert(v = 42)
    }
    logMsg(info, "testCML_choose_never - End");
  }
  
  /*
  testCML_wait() {
    ch is channel()
    cvar1 is mk_cvar()
    var x := 0;
    spawn {
      sleep(100L);
      x := 1;
      cvar_set(cvar1);
    }
    v is prv_sync(wrapPrv(waitPrv(cvar1), (_) => taskReturn(x * 42)))
    assert(v = 42)
  }
  */
  
  serverTask has type (channel of integer) => task of integer;
  serverTask(ch) is let {
    -- a 'tail call'!!!!
    { logMsg(info, "serving.."); }
    rv1 is wrapRv(recvRv(ch), ((v) => (v > 0) ? serverTask(ch) : taskReturn(42)));
    
  } in await(rv1);
  
  testCML_wrapTask() do {
    logMsg(info, "testCML_wrapTask - Start");
    ch is channel();
    _ is __spawnExp((function () is valof {
      -- send 100000...0 to server
      cnt is 100; -- 100000;
      for i in iota(1, cnt, 1) do {
        logMsg(info, "loop $(i)");
        _ is executeTaskOnCurrentThread(send(ch, cnt-i), raiser_fun);
      };
      valis 0;
    }));
    -- serve till end
    v is executeTask(serverTask(ch), raiser_fun);
    assert(v = 42);
    logMsg(info, "testCML_wrapTask - End");
  }

  testTimeout() do {
    logMsg(info, "testTimeout - Start");
    s1 is now();
    t1 is await(timeoutRv(150L));
    _ is executeTask(t1, raiser_fun);
    s2 is now();
    assert(timeDiff(s2, s1) >= 150L);
    
    s3 is timeDelta(now(), 250L);
    t2 is await(atDateRv(s3));
    _ is executeTask(t2, raiser_fun);
    s4 is now();
    assert(s4 >= s3);
    logMsg(info, "testTimeout - End");
  }
  
  import cmlTestAux;
  import cmlTestAux2;
  
  testCrossPackage() do {
    logMsg(info, "testCrossPackage - Start");
    
    ch is channel();
    count is 1000;

    b is "ball";
    -- tasks from different packages
    t1 is throwIn(ch, b, count);
    t2 is throwBack(ch, count);
    
    r is executeTask(
      valof { fut is valof backgroundF(t1);
        valis taskBind(t2,
            (function (_) is
              fut)); }, raiser_fun);
    
    assert(r = b);
    logMsg(info, "testCrossPackage - End");
  }
  
  testCML_executeTask() do {
    logMsg(info, "testCML_executeTask - Start");
    cnt is 1000;
    for i in iota(1, cnt, 1) do {
      _ is executeTaskOnThreadPool(await(timeoutRv(1L)), raiser_fun);
    }
    logMsg(info, "testCML_executeTask - End");
  }
  
  main() do {
    
    testQueue1();
    
    testAtomicRef();

    testCML_create();
    -- testCML_sync();
    testCML_wrap();
    
    testCML_wrap2();
    testCML_wrapTask();
    
    testCML_executeTask();

    testCML_wrap2new(); -- uses task expressions
    -- testCML_wait();
    
    testCML_choose();
    testCML_choose_never();
    
    -- TODO Test always
    
    testTimeout();
    
    -- if it hangs sometimes, atomic references are not working correctly
    testCrossPackage();
    
    logMsg(info, "Done");
  }
}
