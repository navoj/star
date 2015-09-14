whilePerf is package{

  prc unboxed() do {
    var myStr := ""_;
    var i := 0_;
    var startTime := smallest;
    var stopTime  := smallest;
    var keepLooping := true;
    	 
    while keepLooping do {
      if __integer_eq(i,100_) then {
		startTime := nanos();
	  }
	  if __integer_eq(i, 999999_) then {
		stopTime := nanos();
		def timeToProcess is (stopTime - startTime) / 1000000L;
		logMsg(info,"Process time: $timeToProcess milliseconds");
		keepLooping := false;
	  }			
	  myStr := __string_concatenate("Test"_,__integer_string(i));
	  i := __integer_plus(i,1_);						
    }
  }
	
  prc boxed() do {
    var myStr := "";
    var i := 0;
    var startTime := smallest;
    var stopTime  := smallest;
    var keepLooping := true;
    	 
    while keepLooping do {
      if i = 100 then {
		startTime := nanos();
	  }
	  if i = 999999 then {
		stopTime := nanos();
		def timeToProcess is (stopTime - startTime) / 1000000L;
		logMsg(info,"Process time: $timeToProcess milliseconds");
		keepLooping := false;
	  }			
	  myStr := "Test$i";
	  i := i + 1;						
    }
  }
  
  prc main() do {
    boxed();
    unboxed();
  }
};