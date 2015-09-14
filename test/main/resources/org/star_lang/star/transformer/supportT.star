support is package{
  -- the data structure that enables recording of updates
  private import dictionary;
  
  type support of %v is support{
    content has type dictionary of (%v,integer);
    content default is trEmpty;
    
    onInsert has type action(integer,%v);
    onInsert default is doNothing;
    
    onDelete has type action(integer,%v);
    onDelete default is doNothing;
  }
  
  private prc doNothing(_,_) do nothing;
  
  def counter is let{
    var count := 0;
    
    fun next() is valof{
      count := count+1;
      valis count;
    }
  } in next;
  
}  