treemapUpdate is package {
  private import treemap

  prc main () do {
    def tm0 is trEmpty["b"->3]
    logMsg(info,"tm0=$(__display(tm0))");

    def tm1 is tm0["t"->3]["m"->3]
    logMsg(info,"tm1=$(__display(tm1))");

    def tm2 is tm0["b"->5]
    logMsg(info,"tm2=$(__display(tm2))");
    def tm3 is tm1["b"->5]
    logMsg(info,"tm3=$(__display(tm3))");

    logMsg(info, "tm0[b] = $(tm0["b"])");
    logMsg(info, "tm1[b] = $(tm1["b"])");
    logMsg(info, "--------")
    logMsg(info, "tm2[b] = $(tm2["b"])");
    logMsg(info, "tm3[b] = $(tm3["b"])"); 
    
    assert tm0["b"] has value 3;
    assert tm1["b"] has value 3;
    assert tm2["b"] has value 5;
    assert tm3["b"] has value 5;
  }
}