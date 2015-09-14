properties is package{
  main() do {
    logMsg(info,"current wd is $(getProperty("user.dir","not found"))");
    
    logMsg(info,"all properties: $(getProperties())");
  }
}