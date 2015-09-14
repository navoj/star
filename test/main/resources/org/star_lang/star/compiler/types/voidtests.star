voidTests is package {
  
  -- TEST 1
  
  apply_x has type (() => %a) => %a
  fun apply_x(f) is f()

  prc test1() do {
    apply_x((() do nothing)); 
  }
  
  -- TEST 2
  
  type t of %a is t { f has type (integer) => %a }
  
  fun make_t(get_res) is
    t { f = ( (v) => get_res(v)) }
    
  def t0 is make_t(( (i) => 42))
  def t1 is make_t(( (i) do nothing)) 
  def t2 is make_t(( (i) => ()))
  
  fun call_t(tv) is tv.f(13)
  
  prc test2() do {
    ignore call_t(t0); -- ok
    
    call_t(t2);
  }
  
  prc main() do {
    test1();
    test2(); 
  }
}
