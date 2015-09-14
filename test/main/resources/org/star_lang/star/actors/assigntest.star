assigntest is package {
  import actors;
  
  A has type actor of{        
    setA has type action(string);
    getA has type ()=>string; 
    m has type ref string;
  };   
  def A is actor {        
    prc setA(a) do { m := a; };
    fun getA() is m;
    var m := "";       
  };
  
  prc main() do {
    request A's setA to setA("hello");
    def m1 is query A's m with m;
    def m3 is query A's getA with getA();
    
    assert m1=m3;  
  }
}