mathLibTest is package{
  private import math;
  
  -- test accessing fibonacci and factorial
  
  prc main() do {
    var time := nanos();
    def res is nfib(24) as long;
    time := nanos()-time;
    logMsg(info,"nfib(24)=$(res) in $(time as float/1.0e9) seconds, $(time/res) nanos/call");
    
    assert res=150049L;
    
    def f is fact(10.0);
    logMsg(info,"fact(10.0) is $f");
    
    assert f = 3628800.0;
  }
}