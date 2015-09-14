blackWhite is package{

  	type BW is black{
  		name has type string;
  	} or white{
  		name has type string;
  	}or neutral;
  	
  	var myVar := neutral;
  	
  	BWfunc has type () => integer
  	fun BWfunc() where myVar matches black{name=N} is 1
  	 |  BWfunc() where myVar matches white{name=N} is 2
  	-- The problem is with using the same variable name N in both patterns, because if instead of 
  	-- the second case we have the following, everything seems to work correctly:
  	-- BWfunc(myVar) where myVar matches white{name=N2} is 2
  	 |  BWfunc() default is 3


  prc main() do {
  	myVar := white{name="nadal"};
  	def n1 is BWfunc();
  	
  	logMsg(info, "n1 expected: 2 actual: $n1");
  	assert n1=2;
  	
	myVar := black{name="federer"};
	def n2 is BWfunc();
	
	assert BWfunc()=1;
	logMsg(info, "n2 expected: 1 actual: $n2");
  };
}
