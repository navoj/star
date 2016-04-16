worksheet{

  fun ff(K,L) is option computation{
    for (KK,V) in L do{
      if K=KK then
        valis V;
    };
    abort with ()
  };

  fq has type (integer,integer)=>option of boolean
  fun fq(K,L) is option computation{
    if K=L then
      valis true
    abort with ()
  };
  
  fun id(X) is X;
  

  def MM is list of [(1,"alpha"), (2,"beta"), (3,"gamma"), (4,"delta")];

  show "value of ff(2,MM) is $(valof ff(2,MM))"

  assert valof ff(2,MM) = "beta"

  fun test(K,L) is option computation {
    try {
      valis valof fq(K,L)
    } on abort {
      case _ default do
        valis false
    }
  }

  show test(1,1)
  assert test(1,1) has value true

  show test(1,2)
  assert test(1,2) has value false
} 