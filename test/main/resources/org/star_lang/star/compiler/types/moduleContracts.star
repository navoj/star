modules is package{

  contract foo over t determines e is {
    f has type (t)=>e;
  };
  
  implementation foo over cons of integer determines integer is {
    fun f(L) is L[0] or else nonInteger;
  }
  
  type m of t is m{
    k has kind type of type where foo over k of t determines t;
    poot has type (k of t,t)=>k of t;
    c has type k of t;
    ff has type (k of t)=>t;
  };
  
  def M is m{
    type cons counts as k;
    def c is cons of {1;2;3};
    fun poot(L,E) is cons of {E;..L};
    def ff is f;
  }
  
  prc main() do let{
    open M;
  } in 
    { assert f(c)=1 };
}
