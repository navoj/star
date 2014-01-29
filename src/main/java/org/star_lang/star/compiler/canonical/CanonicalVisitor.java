package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;
/**
 * 
 * Copyright (C) 2013 Starview Inc
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

public interface CanonicalVisitor extends ActionVisitor
{
  void visitApplication(Application appl);

  void visitRecord(RecordTerm record);

  void visitRecordPtn(RecordPtn record);

  void visitRecordSubstitute(RecordSubstitute update);

  void visitCaseExpression(CaseExpression exp);

  void visitCastExpression(CastExpression exp);

  void visitCastPtn(CastPtn ptn);

  void visitConditionalExp(ConditionalExp act);

  void visitConditionCondition(ConditionCondition conditionCondition);

  void visitConjunction(Conjunction conjunction);

  void visitContentCondition(ContentCondition cond);

  void visitContractEntry(ContractEntry entry);

  void visitContractImplementation(ImplementationEntry entry);

  void visitDisjunction(Disjunction disjunction);

  void visitFieldAccess(FieldAccess dot);

  void visitMemo(MemoExp memo);

  void visitFalseCondition(FalseCondition falseCondition);

  void visitFunctionLiteral(FunctionLiteral f);

  void visitImplies(Implies implies);

  void visitImportEntry(ImportEntry entry);

  void visitIsTrue(IsTrue i);

  void visitJavaEntry(JavaEntry entry);

  void visitLetTerm(LetTerm let);

  void visitListSearch(ListSearch ptn);

  void visitMatches(Matches matches);

  void visitMatching(MatchingPattern matches);

  void visitMethodVariable(MethodVariable mtd);

  void visitNegation(Negation negation);

  void visitNullExp(NullExp nil);

  void visitOtherwise(Otherwise otherwise);

  void visitOverloaded(Overloaded over);

  void visitOverloadedFieldAccess(OverloadedFieldAccess over);

  void visitOverloadedVariable(OverloadedVariable over);

  void visitPatternAbstraction(PatternAbstraction pattern);

  void visitPatternApplication(PatternApplication apply);

  void visitPredication(Search predication);

  void visitRaiseExpression(RaiseExpression exp);

  void visitReference(Shriek reference);

  void visitRegexpPtn(RegExpPattern ptn);

  void visitResolved(Resolved res);

  void visitScalar(Scalar scalar);

  void visitScalarPtn(ScalarPtn scalar);

  void visitTrueCondition(TrueCondition trueCondition);

  void visitTuple(ConstructorTerm tuple);

  void visitTuplePtn(ConstructorPtn tuplePtn);

  void visitTypeAliasEntry(TypeAliasEntry entry);

  void visitTypeEntry(TypeDefinition entry);

  void visitTypeWitness(TypeWitness witness);

  void visitValofExp(ValofExp val);

  void visitVarEntry(VarEntry entry);

  void visitOpenStatement(OpenStatement open);

  void visitVariable(Variable variable);

  void visitVoidExp(VoidExp exp);

  void visitWherePattern(WherePattern wherePattern);
}
