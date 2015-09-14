typeCon is package {

  contract Monad over %%m of %a is {
    mcreate has type (%a) => %%m of %a;
  }

  maybeCreate has type (%a) => option of %a;
  fun maybeCreate(a) is some(a);

  implementation Monad over option of %a default is {
    mcreate = maybeCreate;
  }

  fun constant(a) is mcreate(a);
  
  fun dbl2(X) is X%X;
  
  prc main () do {
    def thirteen is constant(13);
    
    assert dbl2(3)=zero;
  }
}