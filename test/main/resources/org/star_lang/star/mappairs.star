demo is package{
  -- sample program to check out the match compiler with
  
  type List of %t is empty or pair(%t, List of %t);
 
  mappairs has type (((%s,%t) =>%u),List of %s,List of %t) =>List of %u;
  mappairs(F,empty,_) is empty;
  mappairs(F,pair(H,T),empty) is empty;
  mappairs(F,pair(H,T),pair(A,B)) is pair(F(H,A),mappairs(F,T,B));
  
} 