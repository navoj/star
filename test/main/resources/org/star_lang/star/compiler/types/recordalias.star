recordalias is package {
  type ty of %a is ct {
    v has type ref queue of %a;
  }
  
  f has type action(ty of integer)
  prc f(v) do {
    v.v := _cons(1, v.v);
  }

  g has type action(ty of integer, integer)
  prc g(x, v) do {
    x.v := _cons(v, x.v);
  }
  
  prc main() do {
    def x is ct { v := queue of []; };
    f(x);
    g(x, 5);
  }
}

