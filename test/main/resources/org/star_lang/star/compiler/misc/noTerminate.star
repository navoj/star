noTerminate is package {

  type Store of %a is Empty or Store(%a);

  contract Monad over %%m of %a is {
    bind has type (%%m of %a, ((%a) => %%m of %b)) => %%m of %b;
  }

  type MyMonad of %a is MyMonad { result has type Store of %a };

  maybeBind has type (MyMonad of %a, (%a) => MyMonad of %b) => MyMonad of %b;
  fun maybeBind(mm matching MyMonad { result = result_a }, f) is
    switch a in {
      case Store(val) is f(val);
      case Empty is mm;
    };

  implementation Monad over MyMonad of %a is {
    bind = maybeBind;
  }
}