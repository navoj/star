tplAlias is package {
  type tup of %t is alias of (( %t));
  type t is t{
      f8 has type tup of (string, string);
  }
  
  main() do {
    var tmp is ("q", "Qr");
    var tmp2 is ((tmp));
    tt has type t;
    var tt is t{
      f8=tmp2;
    };
    logMsg(info, "$tt");
  }
}
