TestActor is package {
	
  A has type concurrent actor of {
    tick has type occurrence of string;
    getTick has type () => string;
    doDump has type (string) => ();
  }
	
  def A is concurrent actor {
    on X on tick do logMsg(info, "A received: #X");
    fun getTick() is "getTick() return";
    prc doDump(X) do logMsg(info, "dump value #X");
  };

  prc main() do {
    notify A with "tick" on tick;
    request A's doDump to doDump("dumpIt");
    def T is query A's getTick with getTick();
    assert T = "getTick() return";
  }
	
}
