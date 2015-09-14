macro is package{
  -- define some key contracts for macro processing
  
  type macroStatus is applies(quoted) or notApplies or error(astLocation,string);
  
  type macroFunction is alias of ((quoted)=>macroStatus);
 
  type macroDefinition is macroRule{
    rule has type (quoted)=>macroStatus;
    key has type string;
  };
  
  fun macroKey(nameAst(_,N)) is N
   |  macroKey(boolAst(_,_)) is "%bool"
   |  macroKey(integerAst(_,_)) is "%integer"
   |  macroKey(longAst(_,_)) is "%long"
   |  macroKey(floatAst(_,_)) is "%float"
   |  macroKey(decimalAst(_,_)) is "%decimal"
   |  macroKey(stringAst(_,_)) is "%string"
   |  macroKey(applyAst(_,nameAst(_,N),A)) is "#N%$(size(A))"
   |  macroKey(applyAst(_,O,A)) is "$(macroKey(O))%$(size(A))"
  
  fun macroReplace(Term,MacroProgram) where 
    macroKey(Term) matches Key and Key->Rules in MacroProgram and Rl in Rules and Rl(Term) matches applies(Repl) is macroReplace(Repl,MacroProgram)
   |  macroReplace(Term,MacroProgram) is unpack(Term,MacroProgram)
  
  fun tryMacros(Term,MacroProgram) where 
    macroKey(Term) matches Key and Key->Rules in MacroProgram and Rl in Rules and Rl(Term) matches applies(Repl) is tryMacros(Repl,MacroProgram)
   |  tryMacros(Term,MacroProgram) default is Term

  private 
  fun unpack(applyAst(Loc,Op,Args),MacroProgram) is valof{
        def nOp is macroReplace(Op,MacroProgram);
        def nArgs is replaceAll(Args,MacroProgram);
        valis tryMacros(applyAst(Loc,nOp,nArgs),MacroProgram);
      }
   |  unpack(Term,_) is Term
  
  fun replaceAll(L,P) is map((A)=>macroReplace(A,P),L)  
}
    