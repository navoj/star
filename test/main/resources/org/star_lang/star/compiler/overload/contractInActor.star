contractInActorTest is package {
 contract C1 over %t is {
   cFun has type (%t) => %t;
 }

 implementation C1 over integer is {
   fun cFun(x) is x + 1;
 }

 def X is memo actor {
   prc aFun(x) do {
     def y is cFun(x);
     logMsg(info, "$y");
   }
 }

 prc main() do {
   request X()'s aFun to aFun(1);
 }
}