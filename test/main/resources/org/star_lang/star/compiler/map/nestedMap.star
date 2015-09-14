nestedMap is package{
  MM has type dictionary of (string,dictionary of (string,integer));
  def MM is dictionary of ["alpha" -> dictionary of ["beta"->2, "gamma"->3],
                "delta" -> dictionary of []];
  
  prc main() do {
    assert size(MM) = 2;
    assert size(someValue(MM["alpha"])) = 2;
    
    assert present MM["delta"];
    assert not present MM["beta"];
    assert MM["alpha"] has value I and present I["beta"];
  }
}
