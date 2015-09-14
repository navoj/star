descendingAny is package {
  prc main() do {
    def q is any of X where X in list of [30, 20, 10, 10] order descending by X;
    logMsg(info,"$q");
    assert q has value 30;
  }
}
