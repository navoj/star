charsequence is package{
  def s is string of ['\n', 's','t','r','i','n','g'];
  prc main() do {
    logMsg(info,"$s");
    logMsg(info,"#s");
    
    assert s as string = "\nstring";
  }
}