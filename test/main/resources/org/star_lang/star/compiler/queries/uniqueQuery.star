uniqueQuery is package{
  prc main() do {
    var l:=list of [12,113,8,45,113];
   
    def m is 3 of X where X in l order by X;
  
    logMsg(info,"m=$m");
    assert m = list of [8, 12, 45];
  
    def q is all X where X in l;
    logMsg(info,"q=$q");
   
    assert q=list of [12,113,8,45,113];
  
    def p is unique X where X in l;
    logMsg(info,"p=$p");
    assert p=list of [12, 113, 8, 45];
  }
}