showUri is package{
  def U is "http://www.example.com?V=2.3\#fooFrag" as uri;
  
  prc main() do {
    logMsg(info,"U=#U");
    logMsg(info,"U is really #(__display(U))");
  }
}