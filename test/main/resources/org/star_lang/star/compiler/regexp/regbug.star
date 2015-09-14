regbug is package{
  prc main() do { 
    var testString := "X";
--    logMsg(info, "Atempting match"); 
    
--    logMsg(info,"Fun: $(regFun(testString))");
    if testString matches `X..` then 
      logMsg(info, "Match succeeded")
    else 
      logMsg(info, "Match failed"); 
  };
  
  fun regFun(`X..`) is true
   |  regFun(_) default is false
  
};