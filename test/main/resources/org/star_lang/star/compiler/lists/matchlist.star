matchlist is package{

  fun singletons(R) is list of { all X where (X matching (list of [_])) in R}
  
  prc main() do {
    def RR is list of [ list of [1], list of [2], list of [3]];
    assert singletons(RR) = RR;
  }
}
 