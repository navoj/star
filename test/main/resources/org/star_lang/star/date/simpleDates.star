simpleDates is package{
  -- test simple dates and coercion
  
  def N is now();
  def T is today();
  
  prc main() do {
    logMsg(info,"now is #N");
    logMsg(info,"diff is $(timeDiff(N,T))");
    
    logMsg(info,"now is $(__display(T))");
    logMsg(info,"now is $(__display(T as string))");
    logMsg(info,"now is $(__display((T as string) as date))");
    
    assert ((T as string) as date) = T
    
    logMsg(info,"today is #T");
  }
}