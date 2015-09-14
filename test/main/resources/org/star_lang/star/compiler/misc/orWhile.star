orWhile is package {
  type List of %a is Nil or Cons(%a, List of %a);

  prc loop3() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while l matches Cons(head, tail) or l matches Cons(head,tail) do {
      l := tail;
      logMsg(info, "loop3: l=$(__display(l))");
    };
    assert l=Nil;
  }

  prc main() do {
    loop3();
  }
}