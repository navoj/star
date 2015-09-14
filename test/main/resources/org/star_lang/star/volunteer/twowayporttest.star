import voltwowayrequest;
import ports;

twowayporttest is package{

  def P2 is p0rt{
    on X on ODP2 do logMsg(info,"P2:$(X cast any)");
  };   
  
  def P3 is p0rt{
    on X on DO do logMsg(info,"P3:$(X cast any)");
  };
  
  def P1 is connectp1(P2,P3);
  
  prc main() do {
    request P1's DO to DO("P1 sends greetings");
  }
}