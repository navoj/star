mapeq2 is package{
  type content is clSymbol(string);
  prc main() do {
   def h1 is dictionary of [clSymbol("bar")-> clSymbol("baz"),
              clSymbol("foo")-> clSymbol("bar")];
   def h2 is dictionary of [clSymbol("bar")-> clSymbol("baz"),
              clSymbol("foo")-> clSymbol("bar")];
   logMsg(info, "$h1 = $h2 => $(h1 = h2)");
   assert h1=h2;

   def h3 is dictionary of ["bar"-> "baz",
              "foo"-> "bar"];
   def h4 is dictionary of ["bar"-> "baz",
              "foo"-> "bar"];
   logMsg(info, "$h3 = $h4 => $(h3 = h4)");
   assert h3=h4;
  };
}