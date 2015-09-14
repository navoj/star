recordVarsPtn is package {
  type Foo is foo {
    a has type integer;
    b has type integer;
  };

  type Tree is node1 {
    c has type integer;
  } or node2 {
    d has type integer;
  };

  x has type (integer, Tree) => (Foo, integer);
  fun x(y, node1{c=z}) is (foo{a=1;b=2}, z)
   |  x(y, node2{d=z}) is let {
        def (foo{a=bb;b=cc}, aa) is x(y, node1{c=42})
        def newZ is aa;
      } in (foo{a=1;b=2}, newZ);
  
  prc main() do {
    def dd is node1{def c is 30};
    def ee is node2{def d is 31};
    assert x(2, dd) = (foo{a=1;b=2}, 30);
    assert x(3, ee) = (foo{a=1;b=2}, 42);
  }

}