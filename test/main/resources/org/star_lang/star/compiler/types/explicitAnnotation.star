explicitAnnotation is package{
  type Color is RBR or RBB;
  type RedBlackTree of %a is
      RBE
   or RBT(Color, RedBlackTree of %a, %a, RedBlackTree of %a);

  rbEmpty has type RedBlackTree of %a;
  def rbEmpty is RBE;
  
  prc main() do {
    assert rbEmpty=RBE;
  }
}
