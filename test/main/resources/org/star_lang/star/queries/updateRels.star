updateRels is package{
  -- test the updateable contract over relations

  R has type ref list of ((string,integer));
  var R := list of [
    ("a",1),
    ("b",2),
    ("a",2),
    ("a",3),
    ("b",1)
  ];
  
  var RR := list of [ ("a",1), ("b",2), ("c",3)];
  
  check has type (string,list of ((string,integer))) =>boolean;
  fun check(S,Rr) is (S,_) in Rr;

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
	
    extend R with ("b",3);
    assert pairCheck("b", 3);

    logMsg(info,"R before deleting a's is $R");
    delete ("a",_) in R;
    logMsg(info,"R after deleting \"a\" is $R");

	assert not pairCheck("a", 1);
	assert not pairCheck("a", 2);
	assert not pairCheck("a", 3);
	assert pairCheck("b", 1);
	assert pairCheck("b", 2);
	assert pairCheck("b", 3);
	assert not pairCheck("c", 1);
	assert not pairCheck("a", 4);
	
	update (N,X) in R with (N,X*2);
	logMsg(info,"R after updating \"b\" is $R");
	
	assert pairCheck("b",2);
	assert not pairCheck("b",1);
	assert pairCheck("b",4);
	assert pairCheck("b",6);
    assert not pairCheck("a", 4);
	
    delete ((_,X) where X!=2) in RR;
    logMsg(info,"RR after deleting !=2 is $RR");
    
    assert not ("a", 1) in RR;
    assert not ("c", 3) in RR;
    assert ("b",2) in RR;
    assert (_,2) in RR;
    assert not (_,1) in RR;
  }
}