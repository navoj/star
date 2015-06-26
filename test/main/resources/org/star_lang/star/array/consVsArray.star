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
consVsArray is package{

  fun buildCons(F,T) is valof{
    var L := nil;
    var Ix := F;
    while Ix<T do{
      L := cons(Ix,L);
      Ix := Ix+1;
    }
    valis L
  }
  
  fun buildList(F,T) is valof{
    var L := list of [];
    var Ix := F;
    while Ix<T do{
      L := list of [Ix,..L];
      Ix := Ix+1;
    }
    valis L
  }
  
  fun buildQueue(F,T) is valof{
    var L := queue of [];
    var Ix := F;
    while Ix<T do{
      L := queue of [Ix,..L];
      Ix := Ix+1;
    }
    valis L
  }

  prc walkOver(S) do {
    var SS := S;
    while SS matches sequence of [H,..T] do{
      -- logMsg(info,"got $H");
      SS := T;
    };
  };
  
  prc walkBack(S) do {
    var SS := S;
    while SS matches [T..,H] do{
      -- logMsg(info,"got $H");
      SS := T;
    };
  }; 
  
  prc main() do {
    def count is 1000;
    def startBase is nanos();
    def CL is buildCons(1,count);
    def consMark is nanos();
        
    def LL is buildList(1,count);
    def listMark is nanos();
    def QL is buildQueue(1,count);
    def queueMark is nanos();
    
    def consBuildTime is consMark-startBase;
    def listBuildTime is listMark-consMark;
    def queueBuildTime is queueMark-listMark;
    
    logMsg(info,"cons build time is $consBuildTime");
    logMsg(info,"list build time is $listBuildTime");
    logMsg(info,"queue build time is $queueBuildTime");
    
    def mark is nanos();
    walkOver(CL);
    def mark2 is nanos();
    walkOver(LL);
    def mark4 is nanos();
    walkOver(QL);
    def mark5 is nanos();
    
    def bmark is nanos();
    walkBack(CL);
    def bmark2 is nanos();
    walkBack(QL);
    def bmark3 is nanos();
    
    walkBack(LL); -- cant walk backwards over lists
    def bmark4 is nanos();
    
    def consWalkTime is (mark2-mark) as float;
    def consWalkbackTime is (bmark2-bmark) as float;
    
    logMsg(info,"cons walk time is $consWalkTime");
    logMsg(info,"cons walk back time is $consWalkbackTime");
    
    def listWalkTime is (mark4-mark2) as float;
    def listWalkbackTime is (bmark4-bmark3) as float;
    
    logMsg(info,"list walk time is $listWalkTime");
    logMsg(info,"list walk back time is $listWalkbackTime");
 
    def queueWalkTime is (mark5-mark4) as float;
    def queueWalkbackTime is (bmark3-bmark2) as float;
    
    logMsg(info,"queue walk time is $queueWalkTime");
    logMsg(info,"queue walk back time is $queueWalkbackTime");
    
    logMsg(info,"cons/list build is $(consBuildTime as float/listBuildTime as float)");
    logMsg(info,"cons/list walk is $(consWalkTime/listWalkTime)");
    logMsg(info,"cons/list walk back is $(consWalkbackTime/listWalkbackTime)");
    
    logMsg(info,"cons/queue build is $(consBuildTime as float/queueBuildTime as float)");
    logMsg(info,"cons/queue walk is $(consWalkTime/queueWalkTime)");
    logMsg(info,"cons/queue walk back is $(consWalkbackTime/queueWalkbackTime)");
    
    logMsg(info,"list/queue build is $(listBuildTime as float/queueBuildTime as float)");
    logMsg(info,"list/queue walk is $(listWalkTime/queueWalkTime)");
    logMsg(info,"list/queue walk back is $(listWalkbackTime/queueWalkbackTime)");
  }
}
    