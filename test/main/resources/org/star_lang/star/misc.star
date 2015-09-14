misc is package {

type TestState is
    TestState{
        failures has type TestFailure
    }

type TST of %a is alias of ((TestState) => (TestState, %a))

type Test is TestOne(TST of integer) 

type TestFailure is
    TestFailure{
        test has type Test;
    };


}