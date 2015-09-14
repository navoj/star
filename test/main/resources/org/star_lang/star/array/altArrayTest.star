altArrayTest is package{
  import altarrays;

  prc main() do {
    def A0 is emptyRlist;

    assert isEmpty(A0);
    assert size(A0)=0;

    logMsg(info,"A0=$A0");

    def A1 is rCons("alpha",A0);
    logMsg(info,"A1=$A1");
    def A2 is rCons("beta", A1);
    logMsg(info,"A2=$A2");
    def A3 is rCons("gamma",A2);
    logMsg(info,"A3=$A3");
    def A4 is rCons("delta", A3);
    logMsg(info,"A4=$A4");
  }
}