-- Package of windowed statistical calculations

windowed is package{
  type eventTime is alias of long;
  
  contract stamped of %t is {
    timeStamp has type (%t)=>eventTime;
    sampleBeginTime has type (%t)=>eventTime;
    sampleEndTime has type (%t)=>eventTime;
  }
    
  type windowBuffer of %t is buffer{
    windowLength has type integer;
    data has type list of %t;
  };
  
  
  
  
  
  insertIntoBuffer has type action(ref windowBuffer of %t,%t ) where stamped over %t;
  insertIntoBuffer(Buff,Pkt)
  {
    lowerLimit is sampleEndTime(Pkt)-Buff.windowLength;
    
    var lx:=0;
    for P in Buff.data while P.SampleEndtime<lowerLimit do
      lx := lx+1;
    
    -- logMsg(info,"deleting up until $lx");
    Buff.data[0:lx]:=[];
    Buff.data[$:] := [Pkt];
    
   logMsg(info,"Data now $(Buff.data)");
    
   logMsg(info,"buffer has $(length(Buff.data)) elements");
  };
  
  
  BufferFun has type [long, BufferState] => ((windowBuffer of Statistical) =>double);
  BufferFun(Period,State) is let{
    var lastStartTime := 9223372036854775807;
    var lastEndTime := -9223372036854775807;
    
    fun has type (windowBuffer of Statistical) =>double;
    fun(Buffer) is valof{
      startPeriod is Buffer.data[$].SampleEndtime-Period;
            
      for Pkt in Buffer.data while Pkt.SampleEndtime =< startPeriod do
      {
        if Pkt.SampleEndtime > lastStartTime then
        {
          -- logMsg(info,"subtract $Pkt");
          State.subtractPacket(Pkt)
        }
      };
      lastStartTime := startPeriod;
      
      for Pkt in Buffer.data and Pkt.SampleEndtime>startPeriod do
      {
        if Pkt.SampleEndtime > lastEndTime then
        {
          -- logMsg(info,"add packet $Pkt");
          State.addPacket(Pkt);
        }
      };
      lastEndTime := Buffer.data[$].SampleEndtime;
      
      valis State.readOff();
    }
  } in fun;
   
  Count has type [long] => ((windowBuffer of Statistical) =>double);
  Count(Period) is let{
    var lastCount := 0;
    
    subtractCount has type action(Statistical);
    subtractCount(Pkt)
    {
      -- logMsg(info,"subtracting $Pkt for count");
      lastCount := lastCount - Pkt.Count;
      -- logMsg(info,"lastCount=$lastCount");
    };
    
    addCount has type action(Statistical);
    addCount(Pkt)
    {
      -- logMsg(info,"adding $Pkt for count");
      lastCount := lastCount + Pkt.Count;
      -- logMsg(info,"lastCount=$lastCount");
    };
    
    readOffCount has type () =>double;
    readOffCount() is lastCount; 
  } in BufferFun(Period, BufferState{subtractPacket=subtractCount; addPacket=addCount; readOff=readOffCount});
  
  Average has type [long] => ((windowBuffer of Statistical) =>double);
  Average(Period) is let{
    var lastSum := 0.0;
    var lastCount := 0;
    
    subtractAve has type action(Statistical);
    subtractAve(Pkt)
    {
      -- logMsg(info,"subtracting $Pkt");
      lastSum := lastSum-Pkt.Sum;
      lastCount := lastCount - Pkt.Count;
      -- logMsg(info,"lastSum=$lastSum, lastCount=$lastCount");
    };
    
    addAve has type action(Statistical);
    addAve(Pkt)
    {
      -- logMsg(info,"adding $Pkt");
      lastSum := lastSum+Pkt.Sum;
      lastCount := lastCount + Pkt.Count;
      -- logMsg(info,"lastSum=$lastSum, lastCount=$lastCount");
    };

    readOffAve has type () =>double;
    readOffAve() is lastSum/lastCount;
  } in BufferFun(Period, BufferState{subtractPacket=subtractAve; addPacket=addAve; readOff=readOffAve});
  
  StdDev has type [long] => ((windowBuffer of Statistical) =>double);
  StdDev(Period) is let{
    var lastSum := 0.0;
    var lastSumSq := 0.0;
    var lastCount := 0;
    
    subtract has type action(Statistical);
    subtract(Pkt)
    {
      -- logMsg(info,"subtracting $Pkt");
      lastSum := lastSum-Pkt.Sum;
      lastSumSq := lastSumSq-Pkt.SumSquared;
      lastCount := lastCount - Pkt.Count;
      -- logMsg(info,"lastSum=$lastSum, lastCount=$lastCount");
    };
    
    add has type action(Statistical);
    add(Pkt)
    {
      lastSum := lastSum+Pkt.Sum;
      lastSumSq := lastSumSq+Pkt.SumSquared;
      lastCount := lastCount + Pkt.Count;
    };

    readOff has type () =>double;
    readOff() is let{
      mean is lastSum/lastCount;
    } in sqrt((lastSumSq - lastCount*mean*mean)/lastCount);
    
  } in BufferFun(Period, BufferState{subtractPacket=subtract; addPacket=add; readOff=readOff});
  
  -- This calculation normalizes timestamps to seconds resolution, counting since Jan 1st 2008
   
  Forecast has type [long,long] => ((windowBuffer of Statistical) =>double);
  Forecast(Period,forecastPeriod) is let{
    var sumX := 0a;
    var sumY := 0a;
    var sumX2 := 0a;
    var sumXY := 0a;
    var C := 0;
    var lastTimestamp := 0;
    
    Fudge is 1199174400;  -- Number of seconds at Jan 1st 2008 since Jan 1 1970
    
    subtract has type action(Statistical);
    subtract(Pkt)
    {
      logMsg(info,"subtracting $Pkt");
      SampleTime is (Pkt.SampleEndtime/1000-Fudge)*1.0a;
      
      sumX := sumX-SampleTime;
      Y is Pkt.Sum/Pkt.Count;
      sumY := sumY-Y;
      sumX2 := sumX2-SampleTime*SampleTime;
      sumXY := sumXY-SampleTime*Y;
      C := C-1;
      logMsg(info,"sumX=$sumX, sumY=$sumY, sumX2=$sumX2, sumXY=$sumXY, C=$C");
    };
    
    add has type action(Statistical);
    add(Pkt)
    {
      logMsg(info,"adding $Pkt");
      SampleTime is Pkt.SampleEndtime/1000-Fudge;
      
      sumX := sumX+SampleTime;
      Y is Pkt.Sum/Pkt.Count;
      sumY := sumY+Y;
      sumX2 := sumX2+SampleTime*SampleTime;
      sumXY := sumXY+SampleTime*Y;
      C := C+1;
      lastTimestamp := SampleTime;
      logMsg(info,"sumX=$sumX, sumY=$sumY, sumX2=$sumX2, sumXY=$sumXY, C=$C, forecast=$(readOff())");
    };

    readOff has type () =>double;
    readOff() where C=<1 is 0;
    readOff() is let{
      m is (C*sumXY - sumX*sumY)/(C*sumX2 - sumX*sumX);
      b is (sumY - m*sumX)/C;
    } in m*(lastTimestamp+forecastPeriod/1000) + b;
  } in BufferFun(Period, BufferState{subtractPacket=subtract; addPacket=add; readOff=readOff});
  
  Min has type [long] => ((windowBuffer of Statistical) =>double);
  Min(Period) is let{
    var lastMin := 9223372036854775807;
    var lastStartTime := 9223372036854775807;
    var lastEndTime := -9223372036854775807;

    min has type (windowBuffer of Statistical) =>double;
    min(Buffer) is valof{
      startPeriod is Buffer.data[$].SampleEndtime-Period;
      var updateRequired := false;
         
      for Pkt in Buffer.data while Pkt.SampleEndtime < startPeriod do
      {
        if Pkt.SampleEndtime > lastStartTime then
        {
          -- logMsg(info,"examining $Pkt for subtraction (min)");
          if Pkt.Min=lastMin then
            updateRequired := true;
        } 
      };
      lastStartTime := startPeriod;
      
      if updateRequired then
      {
        lastMin := 9223372036854775807;
        for Pkt in Buffer.data do
        {
          if Pkt.SampleEndtime>startPeriod and Pkt.Min<lastMin then
            lastMin := Pkt.Min;
        }
      }
      else
      {
        -- logMsg(info,"no update needed for min");
        for Pkt in Buffer.data and Pkt.SampleEndtime>lastEndTime do
        {        
          if Pkt.SampleEndtime > startPeriod and Pkt.Min<lastMin then
          {
            -- logMsg(info,"We have a new min: $(Pkt.Min)");
            lastMin := Pkt.Min;
          };
        };
      };
      lastEndTime := Buffer.data[$].SampleEndtime;
      
      -- logMsg(info, "Min is: $lastMin" );
      valis lastMin;
    }
  } in min;
  
  Max has type [long] => ((windowBuffer of Statistical) =>double);
  Max(Period) is let{
    var lastMax := -9223372036854775807;
    var lastStartTime := 9223372036854775807;
    var lastEndTime := -9223372036854775807;

    max has type (windowBuffer of Statistical) =>double;
    max(Buffer) is valof{
      startPeriod is Buffer.data[$].SampleEndtime-Period;
      var updateRequired := false;
         
      for Pkt in Buffer.data while Pkt.SampleEndtime < startPeriod do
      {
        -- logMsg(info,"examining $Pkt for subtraction");
        if Pkt.SampleEndtime > lastStartTime then
        {
          if Pkt.Max=lastMax then
            updateRequired := true;
        } 
      };
      lastStartTime := startPeriod;
      
      if updateRequired then
      {
        lastMax := -9223372036854775807;
        for Pkt in Buffer.data do
        {
          if Pkt.SampleEndtime>startPeriod and Pkt.Max>lastMax then
            lastMax := Pkt.Max;
        }
      }
      else
      {
        for Pkt in Buffer.data and Pkt.SampleEndtime>startPeriod do
        {
          if Pkt.SampleEndtime > lastEndTime and Pkt.Max>lastMax then
            lastMax := Pkt.Max;
        };
      };
      lastEndTime := Buffer.data[$].SampleEndtime;
      
      -- logMsg(info, "Max is: $lastMax" );
      valis lastMax;
    }
  } in max;
  

  createTimedBuffer has type (integer) =>windowBuffer of %t;
  createTimedBuffer(L) is bffer{
    windowLength = L;
    data = []
  };

          
  bufferedContent has type (windowBuffer of Statistical) =>integer;
  bufferedContent(Buff) where Buff.data=[] is 0;
  bufferedContent(Buff) is Buff.data[$].SampleEndtime+1-Buff.data[0].SampleStarttime; -- 1 millisecond of fudge
  
  timePoint has type [Statistical] => double;
  timePoint(Statistical{SampleEndtime=E}) is E;
  
  
  
}