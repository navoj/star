counter is package{
  def nextId is let{
    var count := 0L;
    
    fun next() is valof{
      count := count+1L;
      valis count;
    }
  } in next;
}