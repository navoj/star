mapLoopTest is package {
  prc comp(H1, H2) do {
    var counter := 0;
    
    for(K1->V1 in H1) do {
      logMsg(info, "Loop beginning");
      if (K1->V2 in H2) then {
        nothing;
      } else {
        counter := counter+1;
        logMsg(info, "Not present $K1");
      }
      logMsg(info, "Loop end: $counter");
      assert counter=1;
    }
  }

  prc main() do {
    def H1 is dictionary of ["A"->"0"];
    def H2 is dictionary of ["C"->"0", "D"->"1", "C"->"2"];
    comp(H1, H2);
  }
}