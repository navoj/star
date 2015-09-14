tableReplace is package{
  -- test out removing elements from a list

  R has type ref list of ((string,integer));
  var R := list of [
    ("a",1),
    ("b",2),
    ("a",2),
    ("a",3),
    ("b",1)
  ];
  
  check has type (string,list of ((string,integer))) =>boolean;
  fun check(S,Rr) is ((SS,_) where SS=S) in Rr;

  fun pairCheck(A,B) is (A,B) in R;
  
  main has type action();
  prc main() do {
	  assert pairCheck("a", 1);
	  assert pairCheck("a", 2);
	  assert pairCheck("a", 3);
	  assert pairCheck("b", 1);
	  assert pairCheck("b", 2);
	  assert not pairCheck("c", 1);
	  assert not pairCheck("a", 4);
	
    extend R with ("c",1);
    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");
    assert pairCheck("c", 1);
    assert not pairCheck("a", 4);
    
    update ("a",X) in R with ("d",X);

    logMsg(info,"R after updating \"a\" is $R");

    assert not pairCheck("a", 1);
	  assert not pairCheck("a", 2);
	  assert not pairCheck("a", 3);
	  assert not pairCheck("a", 4);
	
	  assert pairCheck("d", 1);
	  assert pairCheck("d", 2);
	  assert pairCheck("d", 3);
  }
}