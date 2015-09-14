splices is package {
  prc main () do {
    logMsg(info,"testing list splices");
    var a := list of [0,1];
    a[2:2] := list of [99];
    assert a=list of [0,1,99];

    var b := list of [0,1];
    b[2:1] := list of [99];
  }
}
