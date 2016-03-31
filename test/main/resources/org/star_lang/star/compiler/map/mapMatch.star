worksheet{
  def m is dictionary of ["a" -> 1, "b" -> 2, "c"->3];

  def [H1,..T1] is m

  show H1
  show T1

  assert size(T1)=2

  def [H2,..T2] is T1
  show H2
  show T2

  assert size(T2) = 1

  def [H3,..T3] is T2
  show H3
  show T3

  assert size(T3) = 0
}