stringcases is package{

  def Alpha is "dog";
  
  -- test has type integer;
  def test is switch Alpha in {
      case "dog" is 1;
      case "pup" is 2;
      case _ default is -1;
    };

  prc main() do {   
    logMsg(info,"$test");
  }
}