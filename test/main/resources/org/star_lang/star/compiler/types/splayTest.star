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
splayTest is package{
  import splay;

  intPairOrder has type ordering of ((integer,string));
  intPairOrder is ordering{
    lt((X,_),(Y,_)) is X<Y;
    le((X,_),(Y,_)) is X<=Y;
    eq((X,_),(Y,_)) is X=Y;
    ge((X,_),(Y,_)) is X>Y;
    gt((X,_),(Y,_)) is X>=Y;
  };

  splayQ is splayHeap(intPairOrder);

  main() do {
    S0 is splayQ.emptyQ;

    logMsg(info,"S0=$S0");
    
    S1 is splayQ.insertQ((1,"alpha"),S0);
    
    S2 is splayQ.insertQ((3,"gamma"),S1);
    
    S3 is splayQ.insertQ((2,"beta"),S2);
    
    logMsg(info,"S3=$S3");
    
    assert splayQ.firstEl(S3)=(1,"alpha");
    
    assert splayQ.firstEl(splayQ.restQ(S3))=(2,"beta");
    
    assert splayQ.firstEl(splayQ.restQ(splayQ.restQ(S3))) = (3,"gamma");
    
    var QQ := splayQ.emptyQ;
    for ix in iota(0,1000,1) do
      QQ := splayQ.insertQ((ix,"string $ix"),QQ);
    
    def Start is nanos();
    while not splayQ.isEmptyQ(QQ) do {
      def E is splayQ.firstEl(QQ);
      -- logMsg(info,"E is $E");
      QQ := splayQ.restQ(QQ);
    }
    def End is nanos();
    logMsg(info,"took $(End-Start) nano seconds");
    logMsg(info,"took $(End-Start):999.000000; milli seconds");
    
    var RQ := splayQ.emptyQ;
    for ix in iota(0,1000,1) do
      RQ := splayQ.insertQ((ix,"string $ix"),RQ);
      
    def RStart is nanos();
    for ix in iota(0,100000,1) do {
      -- logMsg(info,"head is $(splayQ.firstEl(RQ))");
      RQ := splayQ.insertQ((ix/*random(10000)*/,"next $ix"),splayQ.restQ(RQ));
    }
   
    def REnd is nanos();
      
    logMsg(info,"queue took $(REnd-RStart) nano seconds");
    logMsg(info,"took $(REnd-RStart):999,999.000000; milli seconds");
    
    logMsg(info,"average $((REnd-RStart)/100000l):999,990.000000; milliseconds/entry");
  }
}