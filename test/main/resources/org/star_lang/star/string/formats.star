formats is package{
 -- test the various formatting capabilities
 
  prc main() do {
    def val is 123456;
    def neg is -val;
    
    def five is 5;
    def mfive is -5;
   
    assert "--$val:P999999.99P;--"="-- 1234.56 --";
   
    assert "--$neg:P999999.99P;--"="--(1234.56)--";
   
    assert "--$val:9,999,999.99+;--"="--1,234.56+--";
    assert "--$neg:9,999,999.99+;--"="--1,234.56---";
   
    assert "--$val:+9,999,999.99+;--"="--+1,234.56+--";
    assert "--$neg:+9,999,999.99+;--"="---1,234.56---";

    assert "--$val:-9,999,999.99;--"="-- 1,234.56--";
    assert "--$neg:-9,999,999.99;--"="---1,234.56--";
  
    assert "--$val:00000000+;--"="--00123456+--";
    assert "--$neg:00000000+;--"="--00123456---";
    
    assert "--$five:9900+;--"="--05+--";
    assert "--$mfive:9900+;--"="--05---";
    
    assert "--$five:+  00;--"="--  +05--";
    assert "--$mfive:+  00;--"="--  -05--";

    assert "--$val:+      00;--"="--  +123456--";
    assert "--$neg:+      00;--"="--  -123456--";
    
    logMsg(info,"--$val:0000;--");
    
    assert "--$val:0000;--"="--*Err--";
    
    var Amnt := -100000;
    logMsg(info,"Balance: $Amnt:P999900.00P; remaining");
    assert "Balance: $Amnt:P999900.00P; remaining"="Balance: (1000.00) remaining";
    
    Amnt := 100000;
    logMsg(info,"Balance: $Amnt:P999900.00P; remaining");
    assert "Balance: $Amnt:P999900.00P; remaining"="Balance:  1000.00  remaining";
    
    Amnt := 45;
    logMsg(info,"Balance: $Amnt:P999900.00P; remaining");
    assert "Balance: $Amnt:P999900.00P; remaining"="Balance:  00.45  remaining";
  }
}
