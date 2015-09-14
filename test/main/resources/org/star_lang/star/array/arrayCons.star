arrayCons is package{
  -- test arr consing
  
  prc main() do {
    var A := list of [];
    for i in iota(1,100,1) do
      A := list of [i,..A];
    logMsg(info,"A=$A");
  }
}