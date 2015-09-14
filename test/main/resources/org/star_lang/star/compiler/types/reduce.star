reduce is package {
 contract Reduce over %%f is {
    reducer has type for all a,b such that ((a, b) => b, %%f of a, b) => b;
  };

  implementation Reduce over cons is {
    def reducer is reduceCons;
  } using {
    fun reduceCons(f, nil, b) is b
     |  reduceCons(f, cons(head, tail), b) is f(head, reducer(f, tail, b))
        using {
          fun f1(aa, bb) is reducer(f, aa, bb);
        };
  };
  
  prc main() do {
    def L is cons of [1,2,3,4];
    
    def K is reducer(((X,Y) => X+Y), L, 0);
    
    logMsg(info,"K=$K");
    
    assert K=10;
  }
}