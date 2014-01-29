fiberControlTest is package {

  import task;
  import fibers;
  import cml;
  
  bingForever has type (integer, channel of string) => task of %a;
  bingForever(i, ch) is task {
    logMsg(info, "bing");
    perform send(ch, "bing");
    perform await(timeoutRv(1000L));
    valis valof bingForever(i+1, ch);
  };

  recvTimeout(ch, ms, def) is chooseRv(cons of {
    recvRv(ch);
    wrapRv(timeoutRv(ms), (function (_) is task { valis def }));
  });
    
  fiberControlTest1() {
    ch is channel();
    
    tryGetNext is task {
      logMsg(info, "get next");
      valis valof await(recvTimeout(ch, 1500L, nonString));
    }

    isRunning is task {
      v is valof tryGetNext;
      valis (v != nonString);
    };
    isNotRunning is task {
      v is valof tryGetNext;
      valis (v = nonString);
    };
    ignoreOne is task {
      v is valof tryGetNext;
      valis ();
    };      

    -- start new fiber    
    (r1, fib) is valof backgroundFF(bingForever(1, ch));

    assert(valof isRunning);

    suspend(fib);
    logMsg(info, "suspend called");
	perform ignoreOne; -- might be one last send active
	logMsg(info, "now suspended...?");
    assert(valof isNotRunning);

    resume(fib)
    assert(valof isRunning);
  }
  
  main() do {
    fiberControlTest1();
  }
  
}