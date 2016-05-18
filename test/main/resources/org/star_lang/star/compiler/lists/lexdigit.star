lexdigit is package{

  fun readNumber(Str) is let{
    fun readNum([0c0,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l,Count+1)
     |  readNum([0c1,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+1l,Count+1)
     |  readNum([0c2,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+2l,Count+1)
     |  readNum([0c3,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+3l,Count+1)
     |  readNum([0c4,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+4l,Count+1)
     |  readNum([0c5,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+5l,Count+1)
     |  readNum([0c6,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+6l,Count+1)
     |  readNum([0c7,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+7l,Count+1)
     |  readNum([0c8,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+8l,Count+1)
     |  readNum([0c9,..L],NumSoFar,Count) is readNum(L,NumSoFar*10l+9l,Count+1)
     |  readNum(L,Num,Count) default is Num;
  } in readNum(Str,0l,0)
  
  prc main() do {
    def XX is readNumber(cons of [0c0,0c3,0c4]);
    logMsg(info,"XX=$XX");
    assert XX=34l;
    assert readNumber("34")=34l;
  }
}
