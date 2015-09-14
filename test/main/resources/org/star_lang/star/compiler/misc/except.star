except is package{
  -- Test exception handling  
  fun first(cons(H,_)) is H
  
  prc simpleExcept() do
  {
    try{
      def A is first(nil); -- Should raise an exception
      logMsg(info,"A is $A");
    } catch {
      logMsg(info,"Had an exception");
    }
    logMsg(info,"end simple except");
  }
  
  prc simpleNoExcept() do
  {
    try{
      def A is first(cons of ["alpha"]); -- Should not raise an exception
      logMsg(info,"A is $A");
    } catch {
      logMsg(info,"Had an exception");
    }
    logMsg(info,"end simple noexcept");
  }
  
  prc main() do
  {
    simpleExcept();
    simpleNoExcept();
  }
  
}    