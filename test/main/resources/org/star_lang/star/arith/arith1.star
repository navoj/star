arith1 is package {
  mSum has type (integer)=>integer;
  mPsum has type (integer)=>integer;
  
  fun mPsum(x) where x=1 is 10
   |  mPsum(x) is x * ((mPsum(1)+ x)/2)

  fun mSum(x) is x/2 * mPsum(1) + mPsum(x)
  fun mSum2(x) is  ((x/2) * mPsum(1)) + mPsum(x)
  
  prc main() do {
    var time := nanos();
    var p:=mSum(35);
    var p2:=mSum2(35)
    
    time := nanos()-time;
    logMsg(info,"arith1(35)=$(p) in $(time as float/1.0e9) seconds, $(time) nanos");
    assert p=p2;
    assert p=940;  
  }
}