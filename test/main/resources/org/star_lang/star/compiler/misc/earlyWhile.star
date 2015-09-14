earlyWhile is package {

  type List of %a is Nil or Cons(%a, List of %a);

  prc loop1() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while ((l matches Cons(head, tail)) and true) do {
      l := tail;
      logMsg(info, "loop1: l=$(__display(l))");
    };
    assert l=Nil;
  }

  prc loop2() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while (l matches Cons(head, tail)) do {
      l := tail;
      logMsg(info, "loop2: l=$(__display(l))");
    }
    assert l=Nil;
  }

  prc main() do {
    loop1();
    loop2();
  }
}
