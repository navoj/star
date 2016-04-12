worksheet{
  import maybe;

  ff has type (integer,integer)=>maybe of boolean
  fun ff(K,L) is maybe computation{
    if K=L then
      valis true
    raise "false";
  };

  fun test(K,L) is maybe computation {
    try {
      valis valof ff(K,L)
    } on abort {
      case _ default do
       valis false
    }
  }

  show test(1,1)

  show test(1,2)

}