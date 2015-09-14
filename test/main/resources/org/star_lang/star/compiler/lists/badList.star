badList is package{
  -- should not compile
  fun test(list of [a,..x,b]) is x;
  
  -- another bad list
  def XX is list of [a1,a2..,m,b1,b2,..t];
    
  def c is test(list of [1,2,3,4,5]);
  
  def foo is sequence of [1,2,..3,4];

  prc main() do {
    logMsg(info, "c is $c");
  }
}