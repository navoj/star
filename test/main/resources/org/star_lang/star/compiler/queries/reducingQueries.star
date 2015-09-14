reducingQueries is package{
  def N is list of[
    (1,2),
    (2,1),
    (1,3),
    (2,3),
    (3,2),
    (3,1)
  ];
  
  def C is list of[
    (1,2),
    (2,1),
    (1,3),
    (2,3),
    (4,4),
    (3,1)
  ];
  
  fun plus(X,Y) is valof{
    logMsg(info,"Add $X to $Y");
    valis X+Y;
  }
  
  prc main() do {
  	assert reduction plus of { all X where (X,_) in N } = 12;
  	
  	assert reduction plus of { all X where (X,_) in C } = 13;
  	
  	assert reduction plus of { unique X where (X,_) in C } = 10;
  	
  	logMsg(info,"Q0=$(list of {3 of X where (X,Y) in C order by Y })");
  	
  	logMsg(info,"O=$(reduction plus of { 3 of X where (X,Y) in C order by Y })");
  	
  	assert reduction plus of { 3 of X where (X,Y) in C order by Y } = 6;
  	
  	logMsg(info,display(list of { unique 4 of X where (X,Y) in C }));

  	logMsg(info,display(reduction plus of { unique 4 of X where (X,Y) in C }));
  	
  	assert reduction plus of { unique 4 of X where (X,Y) in C } = 10;
  }
} 