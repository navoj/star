worksheet{
  tst(myColl) is valof {
    var len := 0;
    for s in myColl do
      len := len + size(s);
    valis len;
  };
  
  assert tst(list of ["alpha","beta","gamma"])=14
  
  ignore logMsg(info,"len is $(tst(list of ["alpha","beta","gamma"]))");
}
  