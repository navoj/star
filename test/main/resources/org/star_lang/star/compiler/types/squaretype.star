worksheet{
  type square[%t] is square(%t);
  
  foo has type square[integer]=>integer
  fun foo(square(X)) is X
}