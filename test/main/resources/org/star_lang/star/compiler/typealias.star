typealias is package{
  -- test out some of the type aliasing stuff

  type foo of %t is foo(string,%t);

  type bar is alias of foo of integer;

  type jar of %t is alias of foo of list of %t;

  F1 has type (foo of integer) => integer;
  fun F1(foo(_,X)) is X;

  F2 has type (bar) => integer;
  fun F2(foo(_,X)) is X;

  XX has type jar of integer;
  def XX is foo("hi",list of [1,2,3]);

  FF has type (jar of %s) =>%s;
  fun FF(foo(_,list of [X])) is X;

  main has type action();
  prc main() do {
    logMsg(info,"$(FF(foo("",list of [2])))");
  };
}