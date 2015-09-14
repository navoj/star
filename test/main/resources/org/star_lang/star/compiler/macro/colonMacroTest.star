import colonMacros;

colonMacroTest is package{

  fun Average(Tm) is (Buff) => sum(Buff)/size(Buff);
  
  fun StdDev(Tm) is let{
    fun stdDev(Buff) is valof{
      def Count is size(Buff);
      def M is sum(Buff)/Count;
      valis sqrt(sumSq(Buff)/Count);
    }
  } in stdDev
  
  fun sum(L) is foldF(L,(A,B) => A+B,0)
  
  fun sumSq(L) is foldF(L,(A,B) => A*A+B,0)
  
  foldF has type (list of %e,(%e,%x)=>%x,%x)=>%x
  fun foldF(L,F,I) is valof{
    var XX := I;
    for E in L do
      XX := F(E,XX);
    valis XX;
  }

  def Buffer is list of [1,3,4,2,-1,0,-4,10];
  
  def XX is parseColon((A:Average(3h)) < (B:(3*StdDev(3h))))
  
  prc main() do
  {
    logMsg(info,"$XX");
    
    logMsg(info,"A is $(XX.A)");
    logMsg(info,"B is $(XX.B)");
    logMsg(info,"result is $(XX.result)");
    assert XX.result=true;
  }
}
