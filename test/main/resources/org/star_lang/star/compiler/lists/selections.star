selections is package{

  def testList is list of [1,2,3,4,5,6,7]
  
  def selected is all X where X in testList and X<4
  
  prc main() do {
    logMsg(info,"selected is $selected");

    assert selected=list of [1,2,3]
  };
}