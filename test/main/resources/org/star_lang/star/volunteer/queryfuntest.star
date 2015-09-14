import volfunquery;
import ports;

queryfuntest is package{

  def R1 is p0rt{
    testFun1 has type(string) => integer;
    fun testFun1(S) is size(S);
  }
    
  def R2 is p0rt{
    testFun2 has type(string) => string;
    fun testFun2(S) is S++S;
  }
  
  def Or is connectOr(R1,R2);
  
  prc main() do {
    def A is query Or's testFun1 'n testFun2 with testFun1(testFun2("fred"));
    logMsg(info,"A=$A");
    assert A=8;
  }
}