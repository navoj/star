freebug is package{

  -- outer has type (integer,(integer,integer))=>string;
  fun outer(X,Y1) is let{
    fun inner(U,Y) is switch U in {
      case 0 is Y;
    };
  } in inner(X,"alpha");
  
  prc main() do {
    assert outer(0,(1,2))="alpha";
  }
}