pr2 is package{
  def pr2 is let {
        def id2 is ( (X) => X);
    } in (id2(1), id2(true));
    
  prc main() do {
    assert pr2 = (1,true)
  }
}