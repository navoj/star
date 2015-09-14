intcase is package{
  prc foo(X) where X>0 do
        logMsg(info,"we got positive $X")
   |  foo(0) do
        logMsg(info,"we got zero");
    
  prc main() do {
    foo(1);
    
    foo(0);
    
    foo(-1);
  }
}