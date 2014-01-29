fixedSqrt is package{

  tolerance is 0.0001;
  isCloseEnough(x,y) is abs((x-y)/x) < tolerance;
  
  fixedPoint(f) is let{
    g(First) is let{
      iterate(guess) is valof{
        next is f(guess);
        if isCloseEnough(guess,next) then
          valis next
        else
          valis iterate(next)
      }
    } in iterate(First)
  } in g;
  
  averageDamp(F) is (function(X) is (X+F(X))/2.0);
  
  sqrt(X) is fixedPoint(averageDamp((function(Y) is X/Y)))(1.0)
  
  main() do {
    T is fixedPoint((function(x) is 1.0+x/2.0))(1.0);
    logMsg(info,"T=$T");
    
    S is sqrt(2.0)
    logMsg(info,"S=$S");
  }
}