whileSearch is package{
  fun tt(X) is task{
    valis X+2;
  }
  
  fun ww(X) is task{
    var C := 0;
    while C<10 do{
      def XX is valof tt(C);
      if XX>5 then
        valis XX
      else
        C := C+X;
    }
    valis nonInteger;
  }
  
  prc main() do{
   def ZZ is valof ww(2);
   
   logMsg(info,"ZZ=$ZZ");
     
   assert ZZ = 6;
  }
}