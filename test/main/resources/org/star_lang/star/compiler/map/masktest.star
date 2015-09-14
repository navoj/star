masktest is package{
    private 
    fun commonMask(H1,H2) is valof{
      var M := 0;
      var H := 1_;
      var Lst := 0_;
      -- logMsg(info,"compute common mask of #(integer(H1)) and #(integer(H2))");
      while M<32 do{
        -- logMsg(info,"H=$(integer(H))");
        def H1U is __integer_bit_and(H,H1);
        def H2U is __integer_bit_and(H,H2);
      
        -- logMsg(info,"H1U=$(integer(H1U)), H2U=$(integer(H2U))");
      
        if __integer_eq(H1U,H2U) then {
          Lst := H1U;
          H := __integer_bit_or(__integer_bit_shl(H,1_),1_); --   (H.<<.1)+1;
          M := M+1;
        } else
          valis Lst;
      };
      valis Lst;
    };
  
  fun mask(integer(H1),integer(H2)) is integer(commonMask(H1,H2));
          
  prc main() do {
    for I in iota(0,15,1) do {
      for J in iota(0,15,1) do {
        logMsg(info,"common mask of $I,$J is $(mask(I,J))");
      }
    }
    
    assert mask(1,1)=1;
    assert mask(10,26)=10;
  }
}