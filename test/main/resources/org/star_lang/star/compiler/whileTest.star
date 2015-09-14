whileTest is package{

  main has type action();
  prc main() do
  {
    var LL := list of [(1,true),(2,false),(3,true),(4,false)];
    
    while (2,C) in LL and size(LL)>1 do{
      logMsg(info,"got $C in $LL");
      remove LL[1];
      logMsg(info,"LL now $LL");
    }
  }
}
