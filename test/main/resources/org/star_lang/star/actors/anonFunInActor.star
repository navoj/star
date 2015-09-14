anonFunInActor is package {
 def AA is actor {
   fun fn1(x, y, f) is f(x, y);
   prc act1(x, act) do act(x);
 }
 prc main() do {
   def c is 3;
   def x is query AA's fn1 with fn1(1, 1, (a,b) => a+b*c);
   request AA's act1 to act1("hello", let{ prc proc(cx) do logMsg(info, cx)} in proc);
   assert x = 4;
 }
}