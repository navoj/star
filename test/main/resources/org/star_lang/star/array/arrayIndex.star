arrayIndex is package{
  -- test indexing into arrays
  
  prc main() do {
    II has type ref list of string;
    var II := list of ["alpha", "beta", "gamma", "delta"];
    
    assert II[0] has value "alpha";
    assert II[1] has value "beta";
    assert II[2] has value "gamma";
    assert II[3] has value "delta";
    
    assert II[4] = none;
    assert II[-1] = none
    
    var C := list of [0,1,2];
    
    C[1] := 4;
    assert C=list of [0,4,2];
  }
}