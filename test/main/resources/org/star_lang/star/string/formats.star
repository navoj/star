/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
formats is package{
 -- test the various formatting capabilities
 
  main() do {
    val is 123456;
    neg is -val;
    
    five is 5;
    mfive is -5;
   
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
