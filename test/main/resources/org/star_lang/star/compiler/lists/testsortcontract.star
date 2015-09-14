testsortcontract is package{
  import sorting;
  import person;
  
  def people is list of [someone{name="peter"},
     someone{name="john"},
     someone{name="fred"},
     someone{name="fred"},
     someone{name="andy"},
     noone];
     
  def ints is list of [1,3,-2,45,10];
  
  prc main() do {
    logMsg(info,"sorted $people is\n$(msort(people))");
    logMsg(info,"sorted $ints is\n$(msort(ints))");
    assert msort(ints)=list of [-2,1,3,10,45];
  }
}
  