arraysets is package{
  -- test set operations over arrays
  
  def A is list of ["alpha", "beta", "gamma"];
  
  def B is list of ["alpha", "gamma", "delta"];
  
  prc main() do {
    assert A union A=A;
    
    assert B intersect B=B;
    
    assert A complement A = list of [];
    
    assert size(A union B)=4;
    assert size(A intersect B)=2;
    
    assert A complement B=list of ["beta"];
  }
}