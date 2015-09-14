whileTask is package{
  fun tt(X) is task{
    valis X+2;
  }
  
  fun ww(X) is task{
    var C:=0;
    
    while C<10 do{
      def XX is valof tt(X);
      C := XX+C;
    }
    valis C+1;
  }
  
  prc main() do{
   def ZZ is valof ww(1);
     
   assert ZZ = 13;
  }
}