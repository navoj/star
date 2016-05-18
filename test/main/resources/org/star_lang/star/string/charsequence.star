charsequence is package{
  def s is string of [0c\n, 0cs,0ct,0cr,0ci,0cn,0cg];
  prc main() do {
    logMsg(info,"$s");
    logMsg(info,"#s");
    
    assert s as string = "\nstring";
  }
}