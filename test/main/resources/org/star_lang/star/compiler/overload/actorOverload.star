actorOverload is package{
  contract FooAble over %t is {
    getFoo has type (%t) => integer;
  };

  implementation FooAble over integer is {
    fun getFoo(x) is 42;
  };

  type AA is alias of actor of {
    foo has type occurrence of integer;
    signaled has type ()=>boolean;
  };

  prc main() do {
    X has type AA;
    def X is actor {
      private var S := false;
      on elt on foo do {
        var y := getFoo(elt);
        S := true;
      };
      fun signaled() is S;
    };
    notify X with 1 on foo;
    assert query X with signaled();
  }
}