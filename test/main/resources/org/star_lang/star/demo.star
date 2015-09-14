demo is package{
  -- sample programs to check out the match compiler with
  
  type List of %t is nil or pair(%t, List of %t);
 
  demo2 has type  (List of %s,List of %s) =>List of %s;
  demo2(nil,Y) is Y;
  demo2(XS,nil) is XS;
  demo2(pair(A,B),pair(X,Y)) is pair(A,pair(X,demo2(B,Y)));
 
  chCode has type (integer) =>string;
  chCode(10) is "\n";
  chCode(32) is " ";
  chCode(48) is "0";
  chCode(X) default is "q";
 
} 