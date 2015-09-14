W is package{
  import T;
  
  a has type (integer) => integer;
  fun a(x) is 42; 

  prc main() do {
    def b is t1{f=a;g=3};
    def c is b.f(9);
    assert c = 42;
  }
}