recordThis is package{

  def RR is let{
    def This is memo{
      F=F; G=G;
    }
    
    fun F(X) is let{
      def T is This();
    } in G(X);
    
    fun G(X) is X;
  } in This();
  
  prc main() do {
    assert RR.F(3)=3;
  }
}