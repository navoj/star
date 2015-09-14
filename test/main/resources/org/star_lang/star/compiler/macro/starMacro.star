starMacro is package{
  # test(?A) ==> testStar(A) ## {
    #fun testStar(applyAst(Loc,nameAst(_,Op),Args)) is stringAst(Loc,Op)
      |  testStar(nameAst(Loc,Op)) is stringAst(Loc,Op)
      |  testStar(X) default is stringAst(astLocation(X),display(X));
  }
  
  prc main() do {
    assert test(Op(1,2))="Op"
    
    assert test(Foo)="Foo"
  }
}