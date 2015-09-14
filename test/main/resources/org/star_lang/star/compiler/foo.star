foo is package {
  type one of %a is None or One(%a);
  
  foo has type (one of integer)=>boolean;
  fun foo(None) is false
   |  foo(One(x)) is true

  prc main() do {
    def z1 is foo(One(0));
    logMsg(info, "z1 is $z1");

    def z2 is foo(None);
    logMsg(info, "z2 is $z2");      
  }
}