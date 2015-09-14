fibSets is package{
  def plusOne is set of [
    {left=0; right=1},
    {left=1; right=2},
    {left=2; right=3},
    {left=3; right=4},
    {left=4; right=5},
    {left=5; right=6},
    {left=6; right=7},
    {left=7; right=8},
    {left=8; right=9},
    {left=9; right=10},
    {left=10; right=11},
    {left=11; right=12},
    {left=12; right=13},
    {left=13; right=14},
    {left=14; right=15},
    {left=15; right=16},
    {left=16; right=17},
    {left=17; right=18},
    {left=18; right=19},
    {left=19; right=20},
    {left=20; right=21},
    {left=21; right=22},
    {left=22; right=23},
    {left=23; right=24},
    {left=24; right=25},
    {left=25; right=26},
    {left=26; right=27},
    {left=27; right=28},
    {left=28; right=29},
    {left=29; right=30},
    {left=30; right=31},
    {left=31; right=32},
    {left=32; right=33},
    {left=33; right=34},
    {left=34; right=35},
    {left=35; right=36},
    {left=36; right=37},
    {left=37; right=38},
    {left=38; right=39},
    {left=39; right=40},
    {left=40; right=41},
    {left=41; right=42},
    {left=42; right=43},
    {left=43; right=44},
    {left=44; right=45},
    {left=45; right=46},
    {left=46; right=47},
    {left=47; right=48},
    {left=48; right=49},
    {left=59; right=50}
  ]
  
  def plusTwo is set of [
    {left=0; right=2},
    {left=1; right=3},
    {left=2; right=4},
    {left=3; right=5},
    {left=4; right=6},
    {left=5; right=7},
    {left=6; right=8},
    {left=7; right=9},
    {left=8; right=10},
    {left=9; right=11},
    {left=10; right=12},
    {left=11; right=13},
    {left=12; right=14},
    {left=13; right=15},
    {left=14; right=16},
    {left=15; right=17},
    {left=16; right=18},
    {left=17; right=19},
    {left=18; right=20},
    {left=19; right=21},
    {left=20; right=22},
    {left=21; right=23},
    {left=22; right=24},
    {left=23; right=25},
    {left=24; right=26},
    {left=25; right=27},
    {left=26; right=28},
    {left=27; right=29},
    {left=28; right=30},
    {left=29; right=31},
    {left=30; right=32},
    {left=31; right=33},
    {left=32; right=34},
    {left=33; right=35},
    {left=34; right=36},
    {left=35; right=37},
    {left=36; right=38},
    {left=37; right=39},
    {left=38; right=40},
    {left=39; right=41},
    {left=40; right=42},
    {left=41; right=43},
    {left=42; right=44},
    {left=43; right=45},
    {left=44; right=46},
    {left=45; right=47},
    {left=46; right=48},
    {left=47; right=49},
    {left=48; right=50},
    {left=49; right=51}
  ]
  
  -- You would never do fibonacci like this, but its a test of relation queries...
  
  rfib has type (integer) =>integer
  fun rfib(0) is 1
   |  rfib(1) is 1
   |  rfib(N) where {left=N1;right=N} in plusOne and {left=N2;right=N1} in plusOne is rfib(N1)+fib(N2)
   |  rfib(N) default is -1
  
  fib has type (integer) =>integer;
  fun fib(0) is 1
   |  fib(1) is 1
   |  fib(N) is fib(N-1)+fib(N-2)
  
  prc main() do {
    def S1 is nanos();
    for ix in iota(1,35,1) do{
      logMsg(info,"fib($ix)=$(fib(ix))");
    }
    def S2 is nanos();
    for ix in iota(1,35,1) do{
      logMsg(info,"rfib($ix)=$(rfib(ix))");
    }
    def S3 is nanos();
    logMsg(info,"regular fib took $(S2-S1) nanosecs");
    logMsg(info,"relational  took $(S3-S2) nanosecs");
    
    for ix in iota(15,20,1) do
      assert fib(ix)=rfib(ix);
  }
}
  
  
  