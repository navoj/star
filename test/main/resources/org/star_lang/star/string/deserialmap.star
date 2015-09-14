deserialmap is package{
  def Src is "alpha => 1.2; beta => 2.4; gamma => 3.6; delta=>4.2";
  
  prc main() do {
    def M is list of { all (Key,Value as float) where
             S in splitString(Src,"; *") and
             S matches `(\w+:Key) *=> *(\d+[.]\d+:Value) *` }
    logMsg(info,"M=$M");
    def MM is list of [("alpha", 1.2), ("beta", 2.4), ("gamma", 3.6), ("delta", 4.2)];
    assert M=MM;
  }
}