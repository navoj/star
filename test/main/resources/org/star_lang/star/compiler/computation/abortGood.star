worksheet{
  import good

  ff has type (integer,integer)=>good of boolean
  fun ff(K,L) is good computation{
    if K=L then
      valis true
    abort with "false";
  };

  fun test(K,L) is good computation {
    try {
       valis valof ff(K,L)
    } on abort {
      case E default do{
        logMsg(info,"Recovering with $E")
        valis false
      }
    }
  }

  show test(1,1)

  show test(1,2)
}