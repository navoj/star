taskCondTest is package{
  import task;
  
  fun tt(X) is task{
    valis X+2;
  }
  
  fun cc(X) is task{
    valis X<10;
  }
  
  fun ww(X) is task{
    var C:=0;
    
    while valof tt(C)<10 do{
      def XX is valof tt(X);
      C := XX+C;
     --  logMsg(info,"C=$C");
    }
    valis C+1;
  }
  
  fun vv() is task {
    var Z := 42;
    __stop_here();
    def t is task { Z := 21; valis 0; };
    __stop_here();
    valis false ? valof t : Z;
  };  
  
  prc main() do{
   def ZZ is valof ww(1);
   logMsg(info,"ZZ=$ZZ");
     
   assert ZZ = 10;
   
   def VV is valof vv();
   logMsg(info,"VV=$VV");
   assert VV=42;
  }
}
  
  