arrayliteral is package{
  -- simple test to observe re-use of variable slots
  prc main() do {
    def L is list of ["alpha", "beta", "gamma", "delta", "eta"];
    
    assert L[0] has value "alpha";
  }
}