fingerClassCast is package {
  import finger;

  fun iotaList(a,b,c) is (iota(a,b,c) has type cons of integer);

  prc main() do {
    var st := flFromCons(iotaList(0,90,1));
  }
}