badWhile is package {

  type List of %a is Nil or Cons(%a, List of %a);

  prc main() do {
    var l := Cons(1, Cons(2, Cons(3, Nil)));
    while ((l matches Cons(head, tail)) or false) do {
      l := tail;
      logMsg(info, "loop3: l=$(__display(l))");
    }
  }
}