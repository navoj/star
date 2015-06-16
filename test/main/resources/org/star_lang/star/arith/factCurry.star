factCurry is package{

  fun mapReduce(F,C,Z) is let{
    fun f(a,b) where a>b is Z
     |  f(a,b) is C(F(a),f(a+1,b))
  } in f;
  
  fun fact(N) is mapReduce(id, (*), 1)(1,N);
  
  prc main() do {
    assert fact(5)=120
  }
}