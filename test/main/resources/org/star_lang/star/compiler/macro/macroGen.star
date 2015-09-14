macroGen is package{
  # generate(?Xp) ==> pickup(Xp) ## {
    # foo ==> #$XX;
    # pickup(?X./bar) ==> pickup(X./foo);
    # pickup(?X) ==> { def foo is 3; logMsg(info,"X=$(X)"); assert (any of U where U in X) has value 3 };
  };
  
  prc main() do {
    generate(list of [bar, bar, foo, foo]);
  }
}
  