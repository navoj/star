matchingtest is package{

  testRelation has type ref list of (integer);
  var testRelation := list of [];

  prc conditionalTest() do {
    var tookElse := false;
    if (X matching Y) in testRelation then
      nothing
    else 
      tookElse := true;
    assert tookElse; 
  };

   prc main() do conditionalTest();
 };