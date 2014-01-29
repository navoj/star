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
miniTestSuite is package {

type testState is testState;

/* "test-state transformer", simple state monad */
type test of %a is Test((testState) => (testState, %a));

implementation (computation) over test is {
  _encapsulate(x) is Test((function (st) is (st, x)));
  _abort(msg) is
	 valof {
		logMsg(info, "_abort");
	    raise msg;
	  };
  _handle(E, _) is raise "handle of #(__display(E)) not supported in test suite";
  _combine(Test(f), mf) is
	Test((function (st) is
	  valof {
		(st1, x) is f(st);
		Test(f1) is mf(x);
		valis f1(st1);
	  }));
};

implementation execution over test is {
  _perform(Test(f), _) is /* not really a good idea */
	valof {
	  (_, v) is f(testState);
	  valis v;
	};
}

/** set current test state in running computation */
private setCurrentTestState has type (testState) => test of ();
setCurrentTestState(ts) is valof {
  logMsg(info, "setCurrentTestState0");
  valis Test((function (_) is 
	valof {
	  logMsg(info, "setCurrentTestState");
	  valis (ts, ())
	}))};

main() do {
  Test(f) is
	test computation { 
	  perform setCurrentTestState(testState);
	  logMsg(info,"After test");
	  valis ();
	};
  _ is f(testState);
};
}
