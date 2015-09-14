aggRelations is package{
  -- test out some of the aggregate list stuff
  
  type testType is item{
    pos has type integer;
  } or empty;

  testf has type (list of testType, integer) => list of testType;
  fun testf(Is, P) is list of { all X where (X matching item{pos=P}) in Is};
	
  main has type action();
  prc main() do {
    var ns := list of [1,6,5,3,8,5,7,3,5,4];
	def items is list of {all item{pos=E} where E in ns};
	logMsg(info,"items = $items");
	
	logMsg(info, display(testf(items,3)));
  };
}