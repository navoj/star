others is package{
  def ttVal is valof{
        if ((true matches true) otherwise (true matches true)) then {
            valis true; 
        } else {
            valis false;
        };
    };
    
  prc main() do {
    logMsg(info,"$ttVal");
    assert ttVal;
  }
}