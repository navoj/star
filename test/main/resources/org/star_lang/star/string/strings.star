strings is package{
  conc has type (string,string) =>string;
  fun conc(A,B) is A++B;
  
  foo has type () => string;
  fun foo() is let{
    def AA is "one";
    def BB is "two";
  } in 
   valof{
     logMsg(info,"conc(AA,BB)=$(conc(AA,BB))");
     valis AA;
   }
  
  prc main() do {
    logMsg(info,"strings test: $(foo())");
    assert conc("one","two")="onetwo";
  };
}
