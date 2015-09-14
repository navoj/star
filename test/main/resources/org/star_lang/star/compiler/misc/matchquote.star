matchquote is package{

  prc main() do {
    def P is quote(Alpha in Beta);
    
    assert P matches applyAst(L1, nameAst(L2, "in"), list of [X,Y]) and
           X matches nameAst(_,"Alpha") and
           Y matches nameAst(_,"Beta");
  }
}
 