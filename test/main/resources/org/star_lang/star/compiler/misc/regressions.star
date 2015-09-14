regressions is package{
  fun Ws(Set) is let{
    def M is size(Set) as float;
    def w1 is (M*sumXY(Set)-sumX(Set)*sumY(Set))/(M*sumX2(Set)-sq(sumX(Set)));
    def w0 is sumY(Set)/M - (w1/M)*sumX(Set);
  } in (w1,w0);
    
  fun sq(X) is X*X;
  
  fun sumXY(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do{
      Total := Total+X*Y;
    }
    valis Total;
  };
  
  fun sumX(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+X;
    valis Total;
  }
  
  fun sumY(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+Y;
    valis Total;
  }
  
  fun sumX2(Set) is valof{
    var Total := 0.0;
    for (X,Y) in Set do
      Total := Total+X*X;
    valis Total;
  }
  
  prc main() do {
    def Set is list of [(1.0,2.0), (3.0,5.2), (4.0,6.8), (5.0,8.4), (9.0,14.8)];
    def (W1,W0) is Ws(Set);
    logMsg(info,"w1=$W1,w0=$W0")
  }
}