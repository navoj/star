import worksheet
worksheet{
  LL is cons of {"alpha"; "beta"; "gamma"; "delta"; "eta"}
  
  show LL group by size
  
  RR is relation of {"alpha"; "beta"; "gamma"; "delta"; "eta"}
  
  show RR group by size
  
  assert (RR group by size) = map of {3->relation of {"eta"}; 4 -> relation of {"beta"}; 5->relation of {"alpha"; "delta"; "gamma"}}
  
  AA is list of {"alpha"; "beta"; "gamma"; "delta"; "eta"}
  
 assert (AA group by size) = map of {3->list of {"eta"}; 4 -> list of {"beta"}; 5->list of {"alpha"; "gamma"; "delta"}}
  
}