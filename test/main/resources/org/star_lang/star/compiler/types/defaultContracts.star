defaultContracts is package{

  contract foo over %t is {
    bar has type (%t)=>string;
    
    fun bar(X) default is __display(X)
  }
  
  implementation foo over integer is {
    fun bar(I) is "%$I";
  }
  
  implementation foo over float is {}
  
  prc main() do {
    logMsg(info,bar(12));
    logMsg(info,bar(12.4));
  }
}