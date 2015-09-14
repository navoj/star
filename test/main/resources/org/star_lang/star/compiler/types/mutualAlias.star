mutualAlias is package{
  type args is alias of ((cons of integer, state_func));
  type state_func is alias of ((args) => args);

  type other_state is Baz {
    state_f has type state_func;
  };

  state_func_cons has type (integer, state_func) => state_func;
  fun state_func_cons(i, f) is (((numbers, _)) => (cons(i, numbers), f));
}