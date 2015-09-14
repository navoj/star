comparables is package {
    foo has type (%a, %a)=>%a where comparable over %a;
    fun foo(x, y) is (x>=y) ? x : bar(y,x);

    bar has type (%a,%a)=>%a where comparable over %a; 
    fun bar(y, x) is foo(y,x);

    prc main() do {
      logMsg(info, "$(foo(1,2))");
      assert foo(1,2) = 2;
      logMsg(info, "$(foo(2,1))");
      assert foo(2,1) = 2;
      logMsg(info, "$(foo(1,1))");
      assert foo(1,1) = 1;
    }
}