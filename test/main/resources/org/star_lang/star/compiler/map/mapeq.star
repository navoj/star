mapeq is package{
  prc main() do
  {
    M1 has type dictionary of (string,integer);
    def M1 is dictionary of ["alpha"->1, "beta"->2, "gamma"->3];
    
    def L1 is list of ["alpha", "beta", "gamma"];
    
    def M2 is valof{
      var M := dictionary of [];
      for Ix in iota(1,3,1) do
        M[someValue(L1[Ix-1])]:= Ix;
      valis M
    };
    
    logMsg(info,"M1=$M1");
    logMsg(info,"M2=$M2");
    assert M1=M2;
    
    assert M1!=dictionary of ["alpha"->1];
    assert M1!=dictionary of ["alpha"->1, "beta"->2, "gamma"->3, "delta"->4];
  }
}