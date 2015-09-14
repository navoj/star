matchingRecord is package{
  type foo is foo{
    X has type integer;
    Y has type string;
  }
  
  fun hasY(X matching foo{Y=S}) is S;
  
  -- findK(L,K) where R matching foo{X=K} in L is R;
  
  fun findK(K,cons(H matching foo{X=K},T)) is H
   |  findK(K,cons(_,T)) is findK(K,T)
  
  prc main() do {
    assert hasY(foo{X=23;Y="fred"})="fred";
    
    assert findK(2,cons of [foo{X=1;Y="a"}, foo{X=2;Y="b"}, foo{X=3;Y="c"}]) = foo{X=2;Y="b"};
  }
} 