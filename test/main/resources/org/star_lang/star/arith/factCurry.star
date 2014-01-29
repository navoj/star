factCurry is package{

  mapReduce(F,C,Z) is let{
    f(a,b) where a>b is Z;
    f(a,b) is C(F(a),f(a+1,b))
  } in f;
  
  fact(N) is mapReduce(id, (*), 1)(1,N);
  
  main() do {
    assert fact(5)=120
  }
}