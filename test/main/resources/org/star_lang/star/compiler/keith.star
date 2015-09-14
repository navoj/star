keith is package{
  -- test out some of the basic features of starrules

--  age has type list of ((string,integer));
  def age is list of [
    ("tom",20),
    ("amy",19),
    ("steven",12),
    ("freya",19),
    ("olive",6),
    ("jessica",1)
  ];
  
  def gender is list of[
    ("tom","male"),
    ("amy","female"),
    ("steven","male"),
    ("freya","female"),
    ("olive","female"),
    ("jessica","female")
  ];
  
  def adultMales is all N where (N,A) in age and not (N,"female") in gender and A >=18;
  def childFemales is all N where (N,A) in age and (N,"female") in gender and A < 18;
 
  main has type action();
  prc main() do {
    logMsg(info,"adultMales is $adultMales");
    logMsg(info,"childFemales is $childFemales");
  };
}