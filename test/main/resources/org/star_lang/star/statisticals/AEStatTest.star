/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
statTest is package{
  type Statistical is Statistical{ 
    AppName has type string;
    SourceName has type string;
    TransactionName has type string;
    Sum has type double;
    SumSquared has type double;
    Count has type double;
    Min has type double;
    Max has type double;

    SampleStarttime has type long;
    SampleEndtime has type long;
    Timestamp has type long
  };
  
  type dataClassification is dataClassification{
    AppName has type string;
    SourceName has type string;
    TransactionName has type string;
  };
  
  type controlClassification is controlClassification{
    SourceName has type string;
  };

  type windowBuffer of %t is bffer{
    windowLength has type integer;
    data has type list of %t
  };

  type MIGResult is MIGResult{
    category has type dataClassification;
    eventTimestamp has type long;
    
    conditions has type list of MIGCondition
  } /*where not (doNothing in conditions)*/
  or nothingToReport;
  
  type MIGCondition is MIGCondition{
    labels has type dictionary of (string,double);
    Cond has type string;
    ruleId has type string;
    Priority has type integer;
  } or doNothing;

  type faultRuleConfig of %t is faultRuleConfig{
    id has type string;
    apply has type ((Statistical) =>MIGResult);
    state has type  %t;
  } or emptyConfig;
  
  reconfigure has type [dataClassification, ref of windowBuffer of Statistical] => faultRuleConfig of windowBuffer of Statistical;
  reconfigure(Cl,Buff) where Cl matches dataClassification{AppName=".*"; SourceName=".*"} is let{

    -- rule1 is mig1();
    rule2 is mig2();
    -- rule3 is mig3();
    
    apply has type [Statistical] => MIGResult;
    apply(Pkt) is valof{
      conditions = collect{
        insertIntoBuffer(Buff,Pkt);
         
 /*       C1 = rule1(Buff);
        if C1!=doNothing then
          elemis C1;
*/
        C2 = rule2(Buff);
        if C2!=doNothing then
          elemis C2;
/*
          
        C3 = rule3(Buff);
        if C3!=doNothing then
          elemis C3;
          */
      };
      -- logMsg(info,"buffered content is $(bufferedContent(Buff)/60000) minutes");
     valis (conditions=[] or bufferedContent(Buff)<1h30 ? nothingToReport
               | MIGResult{
                    category = Cl;
                    eventTimestamp = Pkt.SampleEndtime;
                    conditions = conditions      
                 });
    }
  } in valof{
    Buff.windowLength := 1h30m;
    valis faultRuleConfig{apply=apply; state=Buff; id="statTest"};
  };

  reconfigure(_,_) default is emptyConfig;
  
  configure has type [dataClassification] => faultRuleConfig of windowBuffer of Statistical;
  configure(Cl) where Cl matches dataClassification{AppName=".*"; SourceName=".*"} is 
  let{
    var Buffer := createTimedBuffer(13h); -- maximum of mig1 and mig2 rules
  } in
    reconfigure(Cl,Buffer);
  configure(_) default is emptyConfig;
  
  mig1 has type [] => ((windowBuffer of Statistical) => MIGCondition);
  mig1() is let{
    def Afun is Average(12h);
    def Bfun is StdDev(1h);
    def Cfun is StdDev(12h);
    
    evaluator has type (windowBuffer of Statistical) => MIGCondition;
    evaluator(Buffer) is let{
      A is Afun(Buffer);
      B is Bfun(Buffer);
      C is Cfun(Buffer);
    } in
      (A>55 or B>C ?
         MIGCondition{
           labels = dictionary of [ "A" -> A , "B"->B, "C"->C ];
           Cond = "A:Average(12h)>55 or B:StdDev(1h)>C:StdDev(12h)";
           ruleId="mig1";
           Priority = 1;
         }
       | doNothing);
  } in evaluator; 

  mig2 has type [] => ((windowBuffer of Statistical) => MIGCondition);
  mig2() is let{
    Afun is Max(10m);
    Bfun is Forecast(12m,3m);
    
    evaluator has type (windowBuffer of Statistical) => MIGCondition;
    evaluator(Buffer) is let{
      def A is Afun(Buffer);
      def B is Bfun(Buffer);
    } in
      (A>2*B ?
         MIGCondition{
           labels = dictionary of ["A" -> A, "B"->B ];
           Cond = "A:Max(10m)>B:Forecast(12m,3m)";
           ruleId="mig2";
           Priority = 1;
         }
       | doNothing);
  } in evaluator;
  
   
  mig3 has type [] => ((windowBuffer of Statistical) => MIGCondition);
  mig3() is let{
    Afun is Min(4m);
    
    evaluator has type (windowBuffer of Statistical) => MIGCondition;
    evaluator(Buffer) is let{
      def A is Afun(Buffer);
    } in
      (A>0?
         MIGCondition{
           labels = dictionary of ["A" -> A ];
           Cond = "A:Min(4m)>0";
           ruleId="mig1";
           Priority = 1;
         }
       | doNothing);
  } in evaluator; 
  
  
  type BufferState is BufferState{
    subtractPacket has type action(Statistical);
    addPacket has type action(Statistical);
    readOff has type () =>double;
  };
  
  BufferFun has type [long, BufferState] => ((windowBuffer of Statistical) =>double);
  BufferFun(Period,State) is let{
    var lastStartTime := 9223372036854775807;
    var lastEndTime := -9223372036854775807;
    
    fun has type (windowBuffer of Statistical) =>double;
    fun(Buffer) is valof{
      startPeriod is Buffer.data[$].SampleEndtime-Period;
            
      for Pkt in Buffer.data while Pkt.SampleEndtime <= startPeriod do
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
    addAve(Pkt) do {
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
    readOff() where C<=1 is 0;
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

  insertIntoBuffer has type action(ref of windowBuffer of Statistical,Statistical);
  insertIntoBuffer(Buff,Pkt)
  {
    lowerLimit is Pkt.SampleEndtime-Buff.windowLength;
    
    -- logMsg(info,"lowerlimit is $lowerLimit");
    -- logMsg(info,"inserting $Pkt, $((Pkt.SampleEndtime-Pkt.SampleStarttime)/60000.0) min sample");
    
    var lx:=0;
    for P in Buff.data while P.SampleEndtime<lowerLimit do
      lx := lx+1;
    
    -- logMsg(info,"deleting up until $lx");
    Buff.data[0:lx]:=[];
    Buff.data[$:] := [Pkt];
    
   -- logMsg(info,"Data now $(Buff.data)");
    
   -- logMsg(info,"buffer has $(length(Buff.data)) elements");
  };
          
  bufferedContent has type (windowBuffer of Statistical) =>integer;
  bufferedContent(Buff) where Buff.data=[] is 0;
  bufferedContent(Buff) is Buff.data[$].SampleEndtime+1-Buff.data[0].SampleStarttime; -- 1 millisecond of fudge
  
  timePoint has type [Statistical] => double;
  timePoint(Statistical{SampleEndtime=E}) is E;
};