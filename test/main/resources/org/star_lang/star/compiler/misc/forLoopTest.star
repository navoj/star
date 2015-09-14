forLoopTest is package{
  def L is list of [1, 2, 3, 4];
  prc main() do {
    for Ix in L do
      logMsg(info,"Ix=$Ix");
  }
}
    