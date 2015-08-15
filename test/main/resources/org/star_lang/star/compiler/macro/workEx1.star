import worksheet;

worksheet{
  fun fact(0) is 1
   |  fact(N) is N*fact(N-1)
  
  show fact(10);
    
  fun fib(N) where N=<1 is 1
   |  fib(N) is fib(N-1)+fib(N-2)
  
  show fib(20)
  
  assert fact(10) = 3628800
}