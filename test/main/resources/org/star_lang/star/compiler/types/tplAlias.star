tplAlias is package {
  type tup of %t is alias of (( %t));
  type t is t{
      f8 has type tup of (string, string);
  }
  
  main() do {
    def tmp is ("q", "Qr");
    def tmp2 is ((tmp));
    tt has type t;
    def tt is t{
      f8=tmp2;
    };
    logMsg(info, "$tt");
  }
}
