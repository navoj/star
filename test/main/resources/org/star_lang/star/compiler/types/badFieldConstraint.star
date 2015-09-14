badFieldConstraint is package{
  extractFoo has type (t) => f where t implements{ foo has type f };
  fun extractFoo(x) is x.foo;
}