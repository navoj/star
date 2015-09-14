infer is package{
  fun getE(R) is R.e;
  
  def R1 is {e="alpha"; b=23};
  
  def R2 is {b=34};
  
  def R3 is {a="beta"; e="eta"};
  
  type person is someone{ name has type string; age has type integer } or noone;
  
  fun age(R) is R.age;
  
  prc main() do {
    logMsg(info,"R1.e=$(getE(R1))");
    assert getE(R1)="alpha";
    
    -- logMsg(info,"R2.e=$(getE(R2))");
    logMsg(info,"R3.e=$(getE(R3))");
    
    def F is someone{name="fred"; age=23};
    
    logMsg(info,"fred's age is $(age(F))");
    assert age(F)=23;
  }
}