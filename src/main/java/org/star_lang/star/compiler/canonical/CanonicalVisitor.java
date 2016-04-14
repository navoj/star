package org.star_lang.star.compiler.canonical;

/*  * Copyright (c) 2015. Francis G. McCabe  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language governing  * permissions and limitations under the License.  */
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

  void visitConstructor(ConstructorTerm tuple);

  void visitConstructorPtn(ConstructorPtn tuplePtn);

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

  void visitRaiseExpression(AbortExpression exp);

  void visitReference(Shriek reference);

  void visitRegexpPtn(RegExpPattern ptn);

  void visitResolved(Resolved res);

  void visitScalar(Scalar scalar);

  void visitScalarPtn(ScalarPtn scalar);

  void visitTrueCondition(TrueCondition trueCondition);

  void visitTuple(TupleTerm tuple);

  void visitTuplePtn(TuplePtn tuplePtn);

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
