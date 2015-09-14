voidfun is package{
  f has type () => ();
  fun f() is ();
  
  prc main() do {
    assert f()=();
  }
}