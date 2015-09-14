mapseq is package{
  -- test out type inference over sequences
  
  contract mmap over %%s is {
    mapseq has type for all %e,%f such that (%%s of %e,(%e)=>%f) => %%s of %f
  }
  
  implementation mmap over list is {
    fun mapseq(S, F) is valof{
      var alreadyMapped := [];
	  var toBeMapped := S;
	  while toBeMapped matches [X,.. restTBM] do {
	    alreadyMapped := [alreadyMapped..,F(X)];
	    toBeMapped := restTBM;
	  };
	  valis alreadyMapped;
    };
  }
  
  prc main() do {
    def XX is mapseq(list of [1,2,3], ((X) => X*2));
    
    logMsg(info,"XX=$XX");
    
    assert XX = list of [2,4,6];
  }
}  
    