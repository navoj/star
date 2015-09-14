fFluent is package {

  type fIntervalz is alias of relation of fInterval;

  type fFluent is alias of actor of {
    fInitiate has type action(long, any);
    fTerminate has type action(long, (fInterval)=>boolean);
    fForget has type action(long, (fInterval)=>boolean);
--    fAt has type (long, (fInterval)=>boolean)=>fIntervalz;
    fValue has type relation of fInterval;
    fFirstTime has type ()=>long;
    fCurrentTime has type ()=>long;
  };

  type fInterval of %t is fInterval {
    start has type long;
    stop has type long;
    measure has type %t;
  }

  --
  -- Create an instance of a fFluent, which can be used like a new super-powered fluent...
  --

  newfFluent has type ()=>fFluent;
  newfFluent() is actor {
    --
    -- Initiate an interval within a fluent...
    --
    fInitiate(timepoint, newMeasure) do {
      logMsg(info, "I was HERE!!!!!");
      logMsg(info, "EXTENDING FFLUENT with start = $(timepoint), stop = -1, measure = ");
      logMsg(info, "I was also HERE!!!!!");
    
      extend _ffluent with {start = timepoint; stop = -1; measure = newMeasure};
      -- Update our low-water mark...
      if timepoint < _fFirst
        then _fFirst := timepoint;

      -- Update our most-recent high water mark...
      if timepoint > _fRecent
        then _fRecent := timepoint;
      
	  for X in _ffluent do {
	    logMsg(info, "FLUENT CONTAINS INTERVAL - start: $(X.start).   stop: $(X.stop).");
	  };
    };

    --
    -- Terminate an interval within a fluent...
    --
    fTerminate(timepoint, measurePtn) do {
      update (_FF where measurePtn(_FF) and 
      				    timepoint > _FF.start) in _ffluent with { start=_FF.start; 
      				    										  stop=timepoint; 
      				    										  measure=_FF.measure};

      -- Update our most-recent high water mark...
      if timepoint > _fRecent
        then _fRecent := timepoint;
    };

    --
    -- Forget time intervals from a fluent that are older than a specified time...
    --
    -- This is overly simplistic.  It only "forgets" intervals that end before <timepoint>.  In practice
    -- I suspect it should truncate intervals that actually span the timepoint...
    --

    fForget(timepoint, measurePtn) do {
      delete (_FF where measurePtn(_FF) and
      					timepoint > _FF.stop) in _ffluent;
    };

    --
    -- Search the fluent...
    --

    /*
    fAt(timepoint, measurePtn) is all _FF where _FF in _ffluent and
      					   measurePtn(_FF) and
      				       timepoint > _FF.start and
      				       timepoint < _FF.stop;
    */

    fValue is _ffluent;
    
    --
    -- Behavior to return the tick of the first defined interval:
    --

    fFirstTime() is _fFirst;

    --
    -- Behavior to return the tick of the most-recently valid interval:
    --
    fCurrentTime() is _fRecent;
  } using {
    _ffluent has type relation of fInterval;  
    var _ffluent := {};

    var _fFirst := -1L;

    var _fRecent := -1L;
  };
}

