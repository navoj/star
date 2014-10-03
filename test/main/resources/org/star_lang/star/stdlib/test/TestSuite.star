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
TestSuite is package {
import Prelude;
import Matcher;
import PPrint;

#left((docConcat), 40);

type TestState is
	TestState{
		maybeTest has type Maybe of Test
		path has type List of string
		failures has type List of TestFailure
	}

emptyTestState is TestState{ maybeTest = Nothing; path = Null; failures = Null } 

-- "TestStateTransformer"
type TST of %a is alias of ((TestState) => (TestState, %a))

type Assertion is alias of TST of Unit;

/*
    (>>=)   :: m a -> (a -> m b) -> m b  
    (>>)    :: m a -> m b -> m b  
    return  :: a -> m a  
*/


#left((tstBind), 40);
(tstBind) has type (TST of %a, (%a) => TST of %b)
					=> TST of %b
m tstBind f is
	(function(ts) is
		let {
			(newTs, a) is m(ts)
		} in
			(f(a))(newTs))

tstReturn has type (%a) => TST of %a
tstReturn(x) is (function(ts) is (ts, x));

tstCompute has type (() => %a) => TST of %a;
tstCompute(f) is (function (ts) is (ts, f()));

#TstCompute(?exp) ==> tstCompute((function () is ?exp));

#left((tstThen), 40);

(tstThen) has type (TST of %a, TST of %b) => TST of %b
m1 tstThen m2 is m1 tstBind (function(_) is m2)

runTST has type (TST of %a) => %a
runTST(tst) is
	let {
		(_, val) is tst(emptyTestState)
	} in val

type Test is TestOne(TST of Unit) 
		  or TestList(List of Test)
		  or TestLabel(string, Test);

type TestFailure is
	TestFailure{
		test has type Test;
		path has type List of string;
		description has type Doc;
	};

withTest has type (Test, TST of %a) => TST of %a
withTest(t, tst) is
	(function (ts) is
		tst(ts substitute { maybeTest = Just(t) }))

currentTestState has type TST of TestState
currentTestState(ts) is (ts, ts)

currentTest has type TST of Test
currentTest(ts) is (ts, expectJust(ts.maybeTest))
	
currentPath has type TST of List of string
currentPath(ts) is (ts, ts.path)

currentFailures has type TST of (List of TestFailure)
currentFailures(ts) is (ts, ts.failures)

withLabel has type (string, TST of %a) => TST of %a
withLabel(l, tst) is
	(function (ts) is
		let {
			oldPath is ts.path;
			(newTS, val) is tst(ts substitute { path = Cons(l, oldPath) });
		} in
			(newTS substitute { path = oldPath }, val));

assertSomething has type (() => Maybe of Doc) => Assertion;
assertSomething(f) is
	currentTest tstBind
	 (function (t) is
	   currentPath tstBind
	    (function (path) is
	      (function (ts) is
			  case f() in {
				Nothing is (ts, Unit);
				Just(dsc) is
				  (ts substitute { failures = Cons(f, ts.failures) }, Unit)
				  	using {
				  		f is TestFailure{ test = t; path = path; description = dsc}
				  	};
			  }
			)));

runTest has type action(Test)
runTest(tst) do {
	ts is runTST(reallyRunTest(tst))
    reportTestFailures(ts.failures)
}

reallyRunTest has type (Test) => TST of TestState
reallyRunTest(t matching TestOne(tst)) is
	withTest(t,
	(function (ts) is
	  let {
	  	(tsNew, _) is valof {
	  		logMsg(info, "running " ++ pathToString(reverse(ts.path)));
	  		valis tst(ts);
	  	}
	  } in
	  	(tsNew, tsNew)))
reallyRunTest(TestList(Null)) is currentTestState
reallyRunTest(TestList(Cons(t, r))) is
	reallyRunTest(t) tstThen
	reallyRunTest(TestList(r))
	
reallyRunTest(TestLabel(l, t)) is
	withLabel(l, reallyRunTest(t))

reportTestFailures has type action(List of TestFailure);
reportTestFailures(Null) do {
	logMsg(info, "ALL TESTS SUCCEDED.");
};

reportTestFailures(tfs where tfs matches Cons(_, _)) do {
	logMsg(info, "Test failures:");
	forEach(reportTestFailure, tfs)
};

reportTestFailure has type action(TestFailure);
reportTestFailure(TestFailure{test = t; path = p; description = d}) do {
	logMsg(info, pathToString(reverse(p)) ++ ": " ++ docPretty(120, d));
};

pathToString has type (List of string) => string;
pathToString(l) is
	foldLeft((function(s, e) is s ++ ":" ++ e), "", l);  

contract Testable of %t is {
	toTest has type (%t) => Test; 
};

implementation Testable of Test is {
	toTest = (function(t) is t);
}

implementation Testable of List of Test is {
	toTest = (function (l) is TestList(l))
}

implementation Testable of list of Test is {
	toTest = (function (l) is TestList(list_to_List(l)))
}

/*
implementation Testable of (TST of Unit) is {
	test = (function (tst) is TestOne(tst))
}
*/

assertBool has type (string, () => boolean) => Assertion
assertBool(s, b) is assertSomething((function () is b() ? Nothing | Just(docText(s))));
	
#AssertBool(?s, ?exp) ==> assertBool(s, (function () is ?exp));

assertGEqual has type (string, () => %a, (%a, %a) => boolean, () => %a) => Assertion
assertGEqual(s, expected, eq, actual) is
	TstCompute(expected())
		tstBind (function (expectedVal) is
	TstCompute (actual())
		tstBind (function (actualVal) is
	assertSomething((function () is
					  eq(expectedVal, actualVal)
					  ? Nothing
					  | Just(docText(s) docConcat docSeparator(":")
							 docConcat docSeparator("expected:")
							 docConcat docText(display(expectedVal))
							 docConcat docSeparator(" actual:")
							 docConcat docText(display(actualVal)))))));

#AssertEqual(?s, ?exp1, ?exp2) ==>
  assertGEqual(s, (function () is ?exp1), (=), (function () is ?exp2));

#AssertGEqual(?s, ?exp1, ?eq, ?exp2) ==>
  assertGEqual(s, (function () is ?exp1), ?eq, (function () is ?exp2));

-- parameter is equality, not inequality
assertNotGEqual has type (string, () => %a, (%a, %a) => boolean, () => %a) => Assertion -- #### where %a requires {equality; display};
assertNotGEqual(s, expected, eq, actual) is
	TstCompute(expected())
		tstBind (function (expectedVal) is
	TstCompute (actual())
		tstBind (function (actualVal) is
	assertSomething((function () is
					  not eq(expectedVal, actualVal)
					  ? Nothing
					  | Just(docText(s) docConcat docSeparator(":")
							 docConcat docSeparator("expected: something other than")
							 docConcat docText(display(expectedVal))
							 docConcat docSeparator(" actual:")
							 docConcat docText(display(actualVal)))))));

#AssertNotEqual(?s, ?exp1, ?exp2) ==>
  assertNotGEqual(s, (function () is ?exp1), (=), (function () is ?exp2));
#AssertNotGEqual(?s, ?exp1, ?eq, ?exp2) ==>
  assertNotGEqual(s, (function () is ?exp1), ?eq, (function () is ?exp2));

assertThat has type (string, Matcher of %a, () => %a) => Assertion;
assertThat(s, matcher, actual) is
  TstCompute(actual())
	tstBind (function (actualVal) is
  assertSomething((function () is
					case match(matcher, actualVal) in {
					  Nothing is Nothing;
					  Just(dsc) is
						Just(docText(s) docConcat docSeparator(":")
							 docConcat docSeparator("actual value:")
							 docConcat docText(display(actualVal))
							 docConcat docSeparator(" did not match")
							 docConcat dsc);
					})));
#AssertThat(?s, ?matcher, ?exp) ==>
  assertThat(?s, ?matcher, (function () is ?exp));

}
