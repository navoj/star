act0rs is package{
  
  pinger has type (()=>actor of {pong has type action(integer)})=>actor of {ping has type action(integer)};
  fun pinger(A) is actor{
	  on X on ping do {
		if X<300 then{
		  logMsg(info,"$X");
		  notify A() with X+1 on pong;
		}
	  }
	};

  ponger has type (()=>actor of {ping has type action(integer)})=>actor of {pong has type action(integer)};
  fun ponger(A) is actor{
	on X on pong do notify A() with X on ping
  }

  fun K(X) is () => X;

  fun group() is let{
	  def PI is memo pinger(PO);
	  def PO is memo ponger(PI);
  } in PI();

  prc main() do
	  notify group() with 0 on ping;
}