comboPtns is package{
  
  comp has type ((%t) <= %t, (%t) <= %t) => ((%t) <= %t);
  fun comp(P1, P2) is ((X) from P1(P2(X)));
   
  ptn gt5(X) from X where X > 5;
  ptn lt10(X) from X where X < 10;
   
  def smPrimes is list of [2, 3, 5, 7, 11];

  fun medPrimes() is all X where (gt5(X) matching lt10(X)) in smPrimes;
   
  fun medPrimes2() is all X where comp(gt5, lt10)(X) in smPrimes;
   
  def med is comp(gt5, lt10);
  fun medPrimes3() is all X where med(X) in smPrimes;
   
  prc main() do {
    assert medPrimes() = list of [7];
     
    assert medPrimes2() = list of [7];
     
    assert medPrimes3() = list of [7];
  }
}

