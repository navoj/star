arrayFilter is package{
  
  def S is list of [("alpha",1), ("beta",2), ("alpha",0), ("beta",10), ("gamma",1)]
  
  prc main() do {
    assert filter(((_,K)) => K<2, S) = list of [("alpha",1), ("alpha",0), ("gamma",1)]
  }
}