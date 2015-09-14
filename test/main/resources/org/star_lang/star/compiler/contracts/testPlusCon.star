testPlusCon is package{
  import plus;
  
  fooFun has type (%x,%x)=>%y where pluss over (%x,%x) determines %y;
  fun fooFun(X,Y) is plus(X,Y);
  
  prc main() do
  {
    assert fooFun(3,4)=7;
    logMsg(info,"fooFun(4.3,4.5)=$(fooFun(4.3,4.5))");
    assert ceil(fooFun(4.3,4.5)) = 9.0;
  }
}