maploop is package{

  var H := dictionary of [
    "p"->indexed{ ("j",23); ("s",34)},
    "q"->indexed{ ("t",12) }
  ];
  
  prc main() do {
    for K->V in H do{
      for (C,A) in V do
        logMsg(info,"K=$K, C=$C, A=$A");
    }
  }
}