queries is package{
  -- test out some of the basic tables and relations stuff
  
  def N is list of [
    (1,2),
    (2,1),
    (1,3),
    (2,3),
    (3,2),
    (3,1)
  ]

  R has type list of ((string,integer));
  def R is list of [
    ("a",1),
    ("b",2),
    ("c",3),
    ("a",4)
  ];
  
  def S is list of [
    ("a",1),
    ("b",2),
    ("d",3),
    ("a",4),
    ("b",3),
    ("e",2)
  ]
  
  type testType is item{
    pos has type integer;
  } or noItem;

  testf has type (list of testType, integer) => list of testType;
  fun testf(Is, P) is list of {all X where (X matching item{pos=P}) in Is};
  
  var parent := list of [
    {parent="fred"; child="sam"; dob=1}, {parent="fred";child="sue"; dob=2},
    {parent="jane"; child="sam"; dob=1},
    {parent="milo"; child="sue"; dob=2}];
  var male := list of [ "fred", "sam"];
	
  main has type action();
  prc main() do {
    def DD is all (X,Z) where (X,Y) in R and (Z,Y) in S;
    
    def EE is all X where (X,Y) in R and ((Y,_) in N ? X in cons of ["a","b"] : "none" matches X);
    logMsg(info,"EE=$EE"); 
  
    def QQ is all (X,Y,U) where (X,Y) in N and (X>Y ? (U,X) in R : (U,Y) in S);
  
    def QQQ is all (X,Y,U) where (X,U) in R and ((U,_) in N ? (Y,V) in S : "not there" matches Y);
    logMsg(info,"DD is $DD");
    logMsg(info,"QQ is $QQ");
    logMsg(info,"QQQ is $QQQ");
    logMsg(info,"4 of S is $(4 of (X,Y) where (X,Y) in S)");
    
    def Four is 4 of (X,Y) where (X,Y) in S;
    
   	logMsg(info,"Four=$Four");
    assert size(Four)=<4;
    
    logMsg(info,"parent of sam = $(any of P where {parent=P;child="sam"} in parent)");
    
    logMsg(info,"all about parents = $(all (P,C,D) where {parent=P;child=C;dob=D} in parent)");
    
    def PS is someValue(any of P where {parent=P;child="sam"} in parent);
    assert PS in list of ["fred", "jane"];
    
    logMsg(info,"parent of fred = $(any of P where {parent=P;child="fred"} in parent)"); 
    assert (any of P where {parent=P;child="fred"} in parent) = none;
    
    ns has type ref list of ((integer));
    var ns := list of [1,6,5,3,8,5,7,3,5,4];
	def items is list of {all item{pos=E} where E in ns};
	
--	logMsg(info,"items=$items, testf=$(testf(items,3))");
	
	assert I in testf(items,3) implies I matches item{pos=3};
	
	def FP is all FF where FF in parent and FF.parent="fred";
	logMsg(info,"fred parents: $FP");
	
	logMsg(info,"male is $male");
	def FFX is all X where X in male and F in FP and F.child=X
	logMsg(info,"$FFX");
	
	def FFF is list of {all X where X in male and F in (all FF where FF in parent and FF.parent="fred") and F.child=X}
	logMsg(info,"$FFF");
	assert FFF = list of ["sam"];
  };
  
  fun checkF(items) is valof{
    def F is testf(items,3);
    logMsg(info,"F=$F");
    def B is (I in F implies I matches item{pos=3}?true:false);
    logMsg(info,"checking $items:$B");
    valis B
  }
}