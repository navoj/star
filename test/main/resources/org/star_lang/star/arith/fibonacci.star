fibonacci is package{
  import fib;
  
  prc main() do {
    var time := nanos();
    def res is nfib(24) as long;
    time := nanos()-time;
    logMsg(info,"nfib(24)=$(res) in $(time as float/1.0e9) seconds, $(time/res) nanos/call");
    
    assert res=150049L;
    
    assert ifib(46) = 1836311903
  }
}