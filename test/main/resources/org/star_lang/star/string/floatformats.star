formats is package{
 -- test the various formatting capabilities
 
  prc main() do {
    def val is 123004.564;
    def neg is -val;
    
    def large is 5.4561e20;
    def small is -large;
    
    def midl is 34.567;
   
    assert "--$val:P999999.99P;--"="-- 123004.56 --";
    assert "--$neg:P999999.99P;--"="--(123004.56)--";
    
    assert "--$large:+9,900.00e+99;--"="--+5,456.10e+17--";
    
    assert "--$midl:00000.99;--" = "--00034.56--";
  }
}