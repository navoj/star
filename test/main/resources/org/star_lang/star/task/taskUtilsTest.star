taskUtilsTest is package {
  import task;
  
  prc testBackground() do {
    def completorT is backgroundF(taskReturn(21));
    def t is taskBind(completorT,
      ( (completor) =>
        taskBind(completor,
          ((v) => taskReturn(v*2)))));
    def res is executeTask(t, raiser_fun);
    assert(res = 42);
  }

  prc main() do {
    testBackground();
  }
}
