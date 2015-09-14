URITestFile4 is package {
  import "star:starviewinc.com/URITestFile3";
  prc main() do {
    def x is makeFun();
    assert x() = 3;
    logMsg(info, "succeeded");
  }
}