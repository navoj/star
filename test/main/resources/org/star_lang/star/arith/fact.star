fact is package{
 /* three kinds of factorial, this one is float */
  fact has type (float)=>float
  fun fact(0.0) is 1.0
   |  fact(N) where N>0.0 is N*fact(N-1.0)
         -- a line comment

  fct has type (integer)=>integer;-- and one is integer
  fun fct(0) is 1
   |  fct(N) where N>0 is N*fct(N-1)
  
  -- And this one is generic
  fun factorial(N) where N=zero is one
   |  factorial(N) default is N*factorial(N-one)
}