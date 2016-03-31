worksheet{

  fun uu(X) is task{
    valis let {
      fun tt(U) is task { valis U+X }
    } in (valof tt(3)) * 2
  }

  def YY is valof uu(3);
  show YY
  assert YY=12;
}