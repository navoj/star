omegatest is package{
  -- test out some of the features of omega types
  
  contract coll over %%c is {
    transform  has type for all e, f such that (%%c of e, (e)=>f) => %%c of f
  }
  
  implementation coll over cons is {
    fun transform (nil,_) is nil
     |  transform (cons(A,L),F) is cons(F(A),transform (L,F))
  }
  
  contract lifter over %%c is {
    lift has type for all %e, %f such that ((%e)=>%f) => (%%c of %e)=>%%c of %f;
  }
  
  implementation lifter over cons is {
    fun lift(F) is let{
      fun lft(nil) is nil
       |  lft(cons(H,T)) is cons(F(H),lft(T))
    } in lft;
  }
  
  int2String has type (integer)=>string;
  fun int2String(I) is I as string;
  
  fun double(I) is I+I;
  
  def dbl is lift(double);
  
  fun testPlus(I) is valof{
    if I+I=I+I then
      valis true
    else
      valis false
  };
  
  prc main() do {
    def L is cons of [1,2,3];
    def S is transform (L,int2String);
    logMsg(info,"S is $S");
    assert S=cons of ["1","2","3"];
    def II is transform (L,double);
    logMsg(info,"II is $II");
    assert II=cons of [2,4,6];
    
    def D is dbl(L);
    logMsg(info,"D is $D");
    assert D=II;
    
    assert testPlus(3);
  }
}