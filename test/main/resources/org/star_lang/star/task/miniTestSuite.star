miniTestSuite is package {

type testState is testState;

/* "test-state transformer", simple state monad */
type test of %a is Test((testState) => (testState, %a));

implementation (computation) over test is {
  fun _encapsulate(x) is Test((st) => (st, x))
  fun _abort(msg) is
	 valof {
		logMsg(info, "_abort");
	    raise msg;
	  }
  fun _handle(E, _) is raise "handle of #(__display(E)) not supported in test suite"
  fun _combine(Test(f), mf) is
	Test((st) =>
	  valof {
		def (st1, x) is f(st);
		def Test(f1) is mf(x);
		valis f1(st1);
	  });
};

implementation execution over test is {
  fun _perform(Test(f), _) is /* not really a good idea */
	valof {
	  def (_, v) is f(testState);
	  valis v;
	};
}

/** set current test state in running computation */
private setCurrentTestState has type (testState) => test of ();
fun setCurrentTestState(ts) is valof {
  logMsg(info, "setCurrentTestState0");
  valis Test((_) => 
	valof {
	  logMsg(info, "setCurrentTestState");
	  valis (ts, ())
	})};

prc main() do {
  def Test(f) is
	test computation { 
	  perform setCurrentTestState(testState);
	  logMsg(info,"After test");
	  valis ();
	};
  ignore f(testState);
};
}
