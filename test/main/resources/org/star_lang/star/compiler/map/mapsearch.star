mapsearch is package {
 def m is dictionary of ["a" -> 1, "b" -> 2];
 
 prc main() do {
   def bar is let {
     def x is "a";
     def foo is switch m in {
       case a where (x1 where x1=x) -> b in m and b <2 is b;
       case _ default is nonInteger;
     }
   } in foo;
   logMsg(info, "$bar");
   assert bar = 1;
 }
}