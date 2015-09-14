remActor is package{
  import notifyRem;
  
  def N is notes(foo);

  foo has type actor of { x has type occurrence of integer};
  def foo is actor {
    on a on x do
      logMsg(info, "$a");
  }

  prc main() do {
    N.bar();
  }
}