worksheet{

  fun uu(X) is task{
    var Y := 1;

    if X<32 then
      valis X+Y

    valis X
  };

  assert valof uu(10) = 11
}