monadtest is package{
  contract monad over %%c is {
    ret has type for all e such that (e)=>%%c of e;
    bind has type for all e, f such that (%%c of e,(e)=>%%c of f) => %%c of f;
    step has type for all e, f such that (%%c of e, %%c of f) => %%c of f;
    fail has type for all a such that (string) => %%c of a;
    
    fun step(M,N) default is bind(M, ((_) => N));
  }
  
  implementation monad over cons is {
    fun ret(X) is cons of [X];
    fun bind(Ll,F) is let{
      fun apply(nil,A) is flat(A,nil)
       |  apply(cons(E,L), A) is apply(L,cons(F(E),A))
      
      private
      fun flat(nil,A) is A
       |  flat(cons(E,L),A) is flat(L,concat(E,A))
      
      private
      fun concat(nil,A) is A
       |  concat(cons(E,X),Y) is cons(E,concat(X,Y))
    } in apply(Ll,nil);
    
    fun fail(S) is nil;
  }
  
  prc main() do {
    def X0 is cons of ["alpha", "beta"];
    logMsg(info,"X0=$X0");
    def X1 is bind(X0,((X) => cons of [X,X]));
    logMsg(info,"X1=$X1");
    assert X1=cons of ["alpha", "alpha", "beta", "beta"]
  }
}
    