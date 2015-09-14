stringParse is package{
  def A is 1;
  def B is "3";
  
  def Cheese is 23;
  def Ham is 21;
  
  fun priceOf("SKU23") is 34.23
   |  priceOf("SKU21") is 12.00
  
  prc assertEqual(X,Y) do {
    if X!=Y then{
      logMsg(info,"expecting $X, got $Y");
      assert false;
    }
  } 
  
  prc main() do {
    logMsg(info,"C:\\B");
    logMsg(info,"A string$((A as string)++("3\$\n"))B$B");
    
    assertEqual("A string13\$\nB3","A string#((A as string)++("3\$\n"))B#B");
    
    logMsg(info,"price of Cheese is $(priceOf("SKU$Cheese"))");
    
    assert priceOf("SKU$Cheese")=34.23;
  }
}