valofsrch is package{

  type change is changed or noChange;

  fun setAttributes(names, attrs) is valof {
  --    logMsg(info,"Got begin update list $names");
      if "ignre" in names then { valis changed }
      else { valis noChange;}
  }
  
  prc main() do {
    def R is setAttributes(list of ["ignre", "not"], {ignre="nothing"; nt=1});
    assert R = changed;
  }
}
