dateEq is package {
  type r is r{ a has type list of integer; b has type date };
   
  prc main() do {
    assert {a=list of []} = {a=list of []};

    def d is now();
    assert d = d;
    AA has type { a has type list of integer; b has type date };
    def AA is {a=list of [];b=d};
    assert AA = {a=list of [];b=d};
  }
}