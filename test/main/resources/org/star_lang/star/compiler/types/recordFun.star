recordFun is package{
  type bar is bar { foo has type action(integer); };
  main () {
    X is bar{
      foo(1) do nothing;
      foo(3) do foo(1);
      foo(4) do foo(3);
    };
    X.foo(3);
  }
}