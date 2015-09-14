orderedTest is package {

  type ordering is lt or gt or eq;

  contract ordered over %t where equality over %t is {
    compare has type (%t, %t) => ordering;
    minimum has type (%t, %t) => %t;
    fun minimum(x, y) default is
	  switch compare(x, y) in {
	    case lt is x;
	    case eq is x;
	    case gt is y;
	  };
    maximum has type (%t, %t) => %t;
    fun maximum(x, y) default is
 	  switch compare(x, y) in {
	    case lt is y;
	    case eq is x;
	    case gt is x;
	  };
  };
  
  implementation ordered over integer is {
    fun compare(X,X) is eq
     |  compare(X,Y) where X>Y is gt
     |  compare(_,_) default is lt
  };

  foo has type (%t) => boolean where ordered over %t;
  fun foo(x) is x = x;

  prc main() do {
    assert minimum(3,4)=3;
  }
}