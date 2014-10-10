/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
macro is package{
  -- define some key contracts for macro processing
  
  type macroStatus is applies(quoted) or notApplies or error(astLocation,string);
  
  type macroFunction is alias of ((quoted)=>macroStatus);
 
  type macroDefinition is macroRule{
    rule has type (quoted)=>macroStatus;
    key has type string;
  };
  
  macroKey(nameAst(_,N)) is N;
  macroKey(boolAst(_,_)) is "%bool";
  macroKey(integerAst(_,_)) is "%integer";
  macroKey(longAst(_,_)) is "%long";
  macroKey(floatAst(_,_)) is "%float";
  macroKey(decimalAst(_,_)) is "%decimal";
  macroKey(stringAst(_,_)) is "%string";
  macroKey(applyAst(_,nameAst(_,N),A)) is "#N%$(size(A))";
  macroKey(applyAst(_,O,A)) is "$(macroKey(O))%$(size(A))";
  
  macroReplace(Term,MacroProgram) where 
    macroKey(Term) matches Key and Key->Rules in MacroProgram and Rl in Rules and Rl(Term) matches applies(Repl) is macroReplace(Repl,MacroProgram);
  macroReplace(Term,MacroProgram) is unpack(Term,MacroProgram);
  
  tryMacros(Term,MacroProgram) where 
    macroKey(Term) matches Key and Key->Rules in MacroProgram and Rl in Rules and Rl(Term) matches applies(Repl) is tryMacros(Repl,MacroProgram);
  tryMacros(Term,MacroProgram) default is Term;

  private unpack(applyAst(Loc,Op,Args),MacroProgram) is valof{
    nOp is macroReplace(Op,MacroProgram);
    nArgs is replaceAll(Args,MacroProgram);
    valis tryMacros(applyAst(Loc,nOp,nArgs),MacroProgram);
  };
  unpack(Term,_) is Term;
  
  replaceAll(L,P) is map(fn A=>macroReplace(A,P),L);  
}
    