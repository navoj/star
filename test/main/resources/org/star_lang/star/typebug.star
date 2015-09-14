typeBug is package{

  h has type (string)=>string;

  fun h(X) is let{
    Y has type string;
    var Y:= X;
  } in {
    valof{
      for A in {"abc"} do {
        Y := A;
      };
      valis Y;
    }
  }
}