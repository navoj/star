readFile is package{
  import fileio;
  
  main() do {
    R is openReadfile("test/main/resources/org/star_lang/star/file/readFile.star");
    
    while not atEof(R) do{
      logMsg(info,"line is $(readLn(R))");
    }
  }
}