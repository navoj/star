TestActor is package {
  import concurrency;
	
  A has type concurrent actor of {
    tick has type occurrence of string;
    getTick has type () => string;
    doDump has type (string) => ();
  }
	
  A is concurrent actor {
    on X on tick do logMsg(info, "A received: #X");
    getTick() is "getTick() return";
    doDump(X) do logMsg(info, "dump value #X");
  };

  main() do {
    notify A with "tick" on tick;
    request A's doDump to doDump("dumpIt");
    T is query A's getTick with getTick();
    assert T = "getTick() return";
  }
	
}
