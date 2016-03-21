worksheet{

  fun f(Src) is task{
    var C := 0;
    for X in Src do{
      if X<0 then
        valis C+5
       else
         C := C+X
    }
    valis C+1
  }

  assert valof f(list of [1,2,-2,5]) = 8
}