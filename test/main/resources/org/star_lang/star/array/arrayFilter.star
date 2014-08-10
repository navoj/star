arrayFilter is package{
  
  S is list of [("alpha",1), ("beta",2), ("alpha",0), ("beta",10), ("gamma",1)]
  
  main() do {
    assert filter((function((_,K)) is K<2), S) = list of [("alpha",1), ("alpha",0), ("gamma",1)]
  }
}