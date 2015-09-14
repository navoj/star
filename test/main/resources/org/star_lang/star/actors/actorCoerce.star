actorCoerce is package{
  def A is actor{
    fun strFyA(X) is X as string;
  };
  
  def B is actor{
    def rr is {
      fun strFyB(X) is X as string
    }
  }
  
  fun C() is actor{
    def cc is {
      fun strFyC(X) is X as string
    }
  }
  
  prc main() do {
    def XX is query A's strFyA with strFyA(12);
   
    logMsg(info,"XX=$XX");
    assert XX="12";
    
    def YY is query B's rr with rr.strFyB(12);
    logMsg(info,"YY=$YY");
    
    assert YY="12";
    
    def ZZ is query C()'s cc with cc.strFyC(12);
    logMsg(info,"ZZ=$ZZ");
    
    assert ZZ="12";
  }
}