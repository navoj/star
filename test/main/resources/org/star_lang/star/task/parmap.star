parmap is package{
	
  -- this is 'fast' takeuchi, optimizing by hand integer arithmetic
  
  fun takI(x,y,z) where __integer_lt(y,x) is takI(takI(__integer_minus(x,1_),y,z),takI(__integer_minus(y,1_),z,x),takI(__integer_minus(z,1_),x,y))
   |  takI(x,y,z) default is y
  
  fun takk(integer(x),integer(y),integer(z)) is integer(takI(x,y,z))
   
  fun longTak(X) is takk(18,12,6)
    
  fun parmap(F,L) is let{
	fun spread(list of []) is list of []
	 |  spread(list of [X,..Y]) is list of [
		  background task{valis F(X)} ,.. spread(Y)]
			
	fun collect(list of []) is list of []
	 |  collect(list of [T,..Ts]) is list of [valof T,..collect(Ts)]
  } in collect(spread(L));
	
  fun pmap(F,L) is let{
	  fun spread() is list of { all (background task{ valis F(X) }) where X in L }
	  fun collect(LL) is list of { all (valof X) where X in LL }
	} in collect(spread());
	    
  fun listmap(F,list of {}) is list of {}
   |  listmap(F,list of {X;..Y}) is list of {F(X);..listmap(F,Y)}
	
  prc main() do {
	L1 has type list of integer;
	def L1 is iota(1,50,1);
		
	def start1 is nanos();
	def L2 is listmap(longTak, L1)
	def end1 is nanos();
		
	def L3 is parmap(longTak, L1)
	def end2 is nanos();
		
	assert L2=L3;
		
	logMsg(info,"sequential map took $( ((end1-start1) as float)/1.0e9) seconds");
	logMsg(info,"parallel map took $( ((end2-end1) as float)/1.0e9) seconds");
  }
}