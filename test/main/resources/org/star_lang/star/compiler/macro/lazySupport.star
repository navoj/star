lazySupport is package{
  -- support the lazy macro package
  
  fun macroSubstitute(Term,Excl,Sub) is let{
    fun subst(nameAst(Lc,Nm)) where present Excl[Nm] is nameAst(Lc,Nm)
     |  subst(nameAst(Lc,Nm)) where Sub[Nm] has value Rep is Rep
     |  subst(A matching boolAst(_,_)) is A
     |  subst(A matching integerAst(_,_)) is A
     |  subst(A matching longAst(_,_)) is A
     |  subst(A matching floatAst(_,_)) is A
     |  subst(A matching decimalAst(_,_)) is A
     |  subst(A matching quote(_)) is A
     |  subst(applyAst(Lc,Op,Args)) is applyAst(Lc,subst(Op),map(subst,Args))
  } in subst(Term);
}
 