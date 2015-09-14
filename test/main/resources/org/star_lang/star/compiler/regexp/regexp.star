regexp is package{
  -- Test some of the regexp matching stuff
    
  prc main() do {
    def testStr is "alphabeta";
    
    assert testStr matches "alphabeta";

    assert testStr matches `alpha(.*:A)` and A="beta";
    
    assert testStr matches `alpha(.*:Alpha)ta` and Alpha="be";
    
    assert not ( testStr matches `alpha(.*:Alpha)ta` and Alpha="beta");
    
    assert testStr matches `(...(.:A).*:B)` and A="h" and B="alphabeta";
    
    assert testStr matches `(a.*b:A)(.*:B)` and A="alphab" and B="eta";
    
    assert "foooBar|P" matches `(.*:A)\|P` and A="foooBar";
    
    assert lengthString("eta")=3;

    logMsg(info,"MINUTES=$MINUTES");
    logMsg(info,"SECONDS=$SECONDS");
     
    assert MINUTES = 13;
    assert SECONDS = 14;
    
    assert "-35.56e100" matches `(\F:F)` and F as float = -35.56e100;

  }

  -- DO NOT use this as a template for string length :)
  fun lengthString(Str) where Str matches `.(.*:X)` is lengthString(X)+1
   |  lengthString("") is 0;

   def DateStr is "12-21-2009 12:13:14";
   
   def MINUTES is (DateStr matches `.*\:([0-9][0-9]:X)\:.*` ? X as integer : -1);
   def SECONDS is (DateStr matches `.*\d[0-9][\u003A;][0-9][0-9][\u003A;]([0-9][0-9]:X)` ? X as integer : -1);
}
      