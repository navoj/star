modules is package{

  contract foo over t determines e is {
    f has type (t)=>e;
  };
  
  implementation foo over cons of integer determines integer is {
    f(L) is L[0];
  }
  
  type m of t is m{
    k has kind type of type where foo over k of t determines t;
    put has type (k of t,t)=>k of t;
    c has type k of t;
    ff has type (k of t)=>t;
  };
  
  M is m{
    type cons counts as k;
    c is cons of {1;2;3};
    put(L,E) is cons of {E;..L};
    ff is f;
  }
  
  main() do let{
    open M;
  } in 
    { assert f(c)=1 };
}
