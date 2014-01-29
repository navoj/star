/**
 * 
 * Copyright (C) 2013 Starview Inc
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
consVsArray is package{

  buildCons(F,T) is valof{
    var L := nil;
    var Ix := F;
    while Ix<T do{
      L := cons(Ix,L);
      Ix := Ix+1;
    }
    valis L
  }
  
  buildArray(F,T) is valof{
    var L := array of {};
    var Ix := F;
    while Ix<T do{
      L := array of {Ix;..L};
      Ix := Ix+1;
    }
    valis L
  }
  
   buildList(F,T) is valof{
    var L := list of {};
    var Ix := F;
    while Ix<T do{
      L := list of {Ix;..L};
      Ix := Ix+1;
    }
    valis L
  }
  
  buildQueue(F,T) is valof{
    var L := queue of {};
    var Ix := F;
    while Ix<T do{
      L := queue of {Ix;..L};
      Ix := Ix+1;
    }
    valis L
  }

  walkOver(S) do {
    var SS := S;
    while SS matches sequence of {H;..T} do{
      -- logMsg(info,"got $H");
      SS := T;
    };
  };
  
  walkBack(S) do {
    var SS := S;
    while SS matches sequence of {T..;H} do{
      -- logMsg(info,"got $H");
      SS := T;
    };
  }; 
  
  main() do {
    var count is 1000;
    startBase is nanos();
    CL is buildCons(1,count);
    consMark is nanos();
    
    AL is buildArray(1,count);
    
    arrayMark is nanos();
    
    LL is buildList(1,count);
    listMark is nanos();
    QL is buildQueue(1,count);
    queueMark is nanos();
    
    consBuildTime is consMark-startBase;
    arrayBuildTime is arrayMark-consMark;
    listBuildTime is listMark-arrayMark;
    queueBuildTime is queueMark-listMark;
    
    logMsg(info,"cons build time is $consBuildTime");
    logMsg(info,"array build time is $arrayBuildTime");
    logMsg(info,"list build time is $listBuildTime");
    logMsg(info,"queue build time is $queueBuildTime");
    
    mark is nanos();
    walkOver(CL);
    mark2 is nanos();
    walkOver(AL);
    mark3 is nanos();
    walkOver(LL);
    mark4 is nanos();
    walkOver(QL);
    mark5 is nanos();
    
    bmark is nanos();
    walkBack(CL);
    bmark1 is nanos();
    walkBack(AL);
    bmark2 is nanos();
    walkBack(QL);
    bmark3 is nanos();
    
--   walkBack(LL); -- cant walk backwards over lists
--    mark6 is nanos();

    
    consWalkTime is (mark2-mark) as float;
    consWalkbackTime is (bmark1-bmark) as float;
    
    logMsg(info,"cons walk time is $consWalkTime");
    logMsg(info,"cons walk back time is $consWalkbackTime");
    
    arrayWalkTime is (mark3-mark2) as float;
    arrayWalkbackTime is (bmark2-bmark1) as float;
   
    logMsg(info,"array walk time is $arrayWalkTime");
    logMsg(info,"array walk back time is $arrayWalkbackTime");
    
    listWalkTime is (mark4-mark3) as float;
--    listWalkbackTime is (mark6-mark5) as float;
    
    logMsg(info,"list walk time is $listWalkTime");
 --   logMsg(info,"list walk back time is $listWalkbackTime");
 
    queueWalkTime is (mark5-mark4) as float;
    queueWalkbackTime is (bmark3-bmark2) as float;
    
    logMsg(info,"queue walk time is $queueWalkTime");
    logMsg(info,"queue walk back time is $queueWalkbackTime");
    
    logMsg(info,"cons/array build is $(consBuildTime as float/arrayBuildTime as float)");
    logMsg(info,"cons/array walk is $(consWalkTime/arrayWalkTime)");
    logMsg(info,"cons/array walk back is $(consWalkbackTime/arrayWalkbackTime)");
    
    logMsg(info,"cons/queue build is $(consBuildTime as float/queueBuildTime as float)");
    logMsg(info,"cons/queue walk is $(consWalkTime/queueWalkTime)");
    logMsg(info,"cons/queue walk back is $(consWalkbackTime/queueWalkbackTime)");
    
    logMsg(info,"array/queue build is $(arrayBuildTime as float/queueBuildTime as float)");
    logMsg(info,"array/queue walk is $(arrayWalkTime/queueWalkTime)");
    logMsg(info,"array/queue walk back is $(arrayWalkbackTime/queueWalkbackTime)");
    
    logMsg(info,"list/array build is $(listBuildTime as float/arrayBuildTime as float)");
    logMsg(info,"list/array walk is $(listWalkTime/arrayWalkTime)");
--    logMsg(info,"list/array walk back is $(listWalkbackTime/arrayWalkbackTime)");
  }
}
    