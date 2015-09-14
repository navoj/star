complexCond is package{

  def II is list of [1,2,3,4,5,6,7,8]
  
  def EE is list of [0,2,4,6,8,10]
  
  prc even(I) do {
    if I in II and I in EE then
      logMsg(info,"$I is an even number");
  }
  
  prc anyEven() do {
    if I in II and I in EE then
      logMsg(info,"we got even number $I");
  }
  
  prc anyOdd() do {
    if I in II and not I in EE then 
      logMsg(info,"we got odd number: $I");
  }
  
  prc evens(I) where I in II and I in EE do 
        logMsg(info,"$I was evens")
   |  evens(I) default do
        logMsg(info,"$I is not evens");
  
  prc main() do {
    even(1);
    even(2);
    even(4);
    even(10);
    even(0);
    
    anyEven();
    
    anyOdd();
    
    evens(1);
    evens(2);
    evens(10);
  }
}