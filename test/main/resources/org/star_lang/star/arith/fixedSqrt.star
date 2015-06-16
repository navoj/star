fixedSqrt is package{

  def tolerance is 0.0001;
  fun isCloseEnough(x,y) is abs((x-y)/x) < tolerance;
  
  fun fixedPoint(f) is let{
    fun g(First) is let{
      fun iterate(guess) is valof{
        def next is f(guess);
        if isCloseEnough(guess,next) then
          valis next
        else
          valis iterate(next)
      }
    } in iterate(First)
  } in g;
  
  fun averageDamp(F) is (X) => (X+F(X))/2.0;
  
  fun sqrt(X) is fixedPoint(averageDamp((Y) => X/Y))(1.0)
  
  prc main() do {
    def T is fixedPoint((x) => 1.0+x/2.0)(1.0);
    logMsg(info,"T=$T");
    
    def S is sqrt(2.0)
    logMsg(info,"S=$S");
  }
}