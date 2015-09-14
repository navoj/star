extractFoo is package{
  extractFoo has type for all f,t such that (t) => f where t implements{ foo has type f };
  fun extractFoo(x) is x.foo;
  
  type foo is foo{
    foo has type string;
  } or bar{
    foo has type string;
    bb has type integer;
  }

  prc main() do{
    def FF is { foo = 23; bar = "alpha"};
    
    assert extractFoo(FF)=23;
    
    def GG is foo{foo="beta"}
    
    assert extractFoo(GG)="beta"
  }
} 