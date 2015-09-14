worksheet{
  fun primes(Max) is let{
    fun cascade(F,K) is (X)=>(F(X) and X%K!=0)
    fun prStep((P,F),X) where F(X) is (list of [P..,X],cascade(F,X))
     |  prStep((P,F),_) is (P,F)
    fun sieve(C) is first(leftFold(prStep,(list of [],(K)=>K%2!=0),C))
    fun first((L,R)) is L
  } in sieve(range(3,Max,2))

  show primes(1000)

}