localActions is package{
  {
    logMsg(info,hello());
  };
  
  fun hello() is "hello world";
  
  prc main() do
    logMsg(info,"again");
}