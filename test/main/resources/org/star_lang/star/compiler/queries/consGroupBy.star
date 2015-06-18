worksheet{
  def LL is cons of ["alpha", "beta", "gamma", "delta", "eta"]
  
  show LL group by size
  
  def RR is list of ["alpha", "beta", "gamma", "delta", "eta"]
  
  show RR group by size
  
  assert (RR group by size) = dictionary of {3->list of ["eta"]; 4 -> list of ["beta"]; 5->list of ["alpha", "delta", "gamma"]}
  
  def AA is list of {"alpha"; "beta"; "gamma"; "delta"; "eta"}
  
  assert (AA group by size) = dictionary of {3->list of ["eta"]; 4 -> list of ["beta"]; 5->list of ["alpha", "gamma", "delta"]}
  
}