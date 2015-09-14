sortlist is package{

  def R is list of [(1,2),(3,4),(1,2)];
  def L is sort(R,<);
  

  prc main() do {
    logMsg(info, "L is $L");
    assert L = list of [(1,2), (1,2), (3,4) ];
  };
}