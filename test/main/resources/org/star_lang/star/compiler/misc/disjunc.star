disjunc is package{
  prc main() do {
    if 3>4 or 2>5 or false then
      logMsg(info,"yes")
    else
      logMsg(info,"no")
  }
}