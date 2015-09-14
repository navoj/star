anonFun is package {
  fun foo() is let {
    def bar is () => 3;
  } in bar;
  
  fun ffoo() is let{
    fun bar() is 3;
  } in bar;
  
  fun inc(X) is (Y) => X+Y;
 
  prc main() do {
    logMsg(info,"foo is $(foo()())");
    logMsg(info,"ffoo is $(ffoo()())");
    
    assert inc(3)(2)=5;
  }
}