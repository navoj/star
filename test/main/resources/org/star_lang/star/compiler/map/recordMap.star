recordMap is package{
  type r is r{
    f has type ref map of (string,integer)
  }
  
  var R is r{ f := map of {"alpha"->1} }
  
  main() do {
    R.f["beta"] := 2
    
    assert R.f["alpha"] = 1
    assert R.f["beta"] = 2
  }
}
    