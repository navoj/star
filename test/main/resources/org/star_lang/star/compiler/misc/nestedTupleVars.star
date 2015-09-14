nestedTupleVars is package{
  main has type action();
  prc main() do {
    def (a, (b, c)) is (1, (2, 3));
    logMsg(info,"a=$(a), b=$(b), c=$(c)");
     
    assert a=1;
    assert b=2;
    assert c=3;
  }
}
