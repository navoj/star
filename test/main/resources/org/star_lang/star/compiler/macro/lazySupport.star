lazySupport is package{
  -- support the lazy macro package
  
  macroSubstitute(Term,Excl,Sub) is let{
    subst(nameAst(Lc,Nm)) where present Excl[Nm] is nameAst(Lc,Nm);
    subst(nameAst(Lc,Nm)) where Sub[Nm] matches Rep is Rep;
    subst(A matching boolAst(_,_)) is A;
    subst(A matching charAst(_,_)) is A;
    subst(A matching integerAst(_,_)) is A;
    subst(A matching longAst(_,_)) is A;
    subst(A matching floatAst(_,_)) is A;
    subst(A matching decimalAst(_,_)) is A;
    subst(A matching quote(_)) is A;
    subst(applyAst(Lc,Op,Args)) is applyAst(Lc,subst(Op),_map(Args,subst))
  } in subst(Term);
}
 