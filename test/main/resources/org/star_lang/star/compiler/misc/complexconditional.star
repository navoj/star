complexconditional is package{
  def R is list of [1,2,3];
  
  prc main() do {
    def FF is (X where X>2) in R ? X : nonInteger;
    
    assert FF=3
  }
} 