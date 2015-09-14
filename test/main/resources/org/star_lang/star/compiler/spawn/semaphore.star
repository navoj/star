semaphore is package{
  
  type sem is alias of  { grab has type ()=>task of (()); release has type ()=>task of (()) };

  semaphore has type (integer) => sem;
  fun semaphore(Count) is {
    private def grabCh is channel();
    private def releaseCh is channel();
    
    { ignore background semLoop(Count); }
    
    private
    fun releaseR(x) is choose wrap incoming releaseCh in ((_) => semLoop(x+1));
    private
    fun grabR(x) is choose wrap incoming grabCh in ((_) => semLoop(x-1));
   
    private 
    fun semLoop(0) is wait for releaseR(0)
     |  semLoop(x) default is wait for grabR(x) or releaseR(x)

    fun grab() is wait for put () on grabCh;
    fun release() is wait for put () on releaseCh;
  };
}
    