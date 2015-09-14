actorupdate is package {

  type Foo is foo {
    b has type list of string;
  };

  prc main() do {
    def X is let {
      type FooActor is alias of actor of {
        Y has type occurrence of integer;
        f has type ref list of Foo;
      }
      aoeu has type FooActor;

      def aoeu is actor {
        var f := list of [
	      foo{def b is list of ["fo","bar"]},
	      foo{def b is list of ["foo","baz"]}
	    ];
        on elt on Y do {
	      update (x matching foo{b=B} where "foo" in B) in f with x substitute{b=list of []};
	      logMsg(info, "$f");
	    };
      }
    } in aoeu;
    notify X with 1 on Y;
  }
}