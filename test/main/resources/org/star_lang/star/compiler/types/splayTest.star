splayTest is package{
  import splay;

  intPairOrder has type ordering of ((integer,string));
  def intPairOrder is ordering{
    fun lt((X,_),(Y,_)) is X<Y;
    fun le((X,_),(Y,_)) is X=<Y;
    fun eq((X,_),(Y,_)) is X=Y;
    fun ge((X,_),(Y,_)) is X>Y;
    fun gt((X,_),(Y,_)) is X>=Y;
  };

  def splayQ is splayHeap(intPairOrder);

  prc main() do {
    def S0 is splayQ.emptyQ;

    logMsg(info,"S0=$S0");
    
    def S1 is splayQ.insertQ((1,"alpha"),S0);
    
    def S2 is splayQ.insertQ((3,"gamma"),S1);
    
    def S3 is splayQ.insertQ((2,"beta"),S2);
    
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