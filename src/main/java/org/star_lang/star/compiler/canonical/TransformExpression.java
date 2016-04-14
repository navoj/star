package org.star_lang.star.compiler.canonical;

public interface TransformExpression<A, E, P, C, D, T>
{
  E transformApplication(Application appl, T context);

  E transformRecord(RecordTerm record, T context);

  E transformRecordSubstitute(RecordSubstitute update, T context);

  E transformCaseExpression(CaseExpression exp, T context);

  E transformCastExpression(CastExpression exp, T context);

  E transformConditionalExp(ConditionalExp act, T context);

  E transformConstructor(ConstructorTerm tuple, T context);

  E transformContentCondition(ContentCondition cond, T context);

  E transformFieldAccess(FieldAccess dot, T context);

  E transformMemo(MemoExp memo, T context);

  E transformMethodVariable(MethodVariable var, T context);

  E transformNullExp(NullExp nil, T context);

  E transformFunctionLiteral(FunctionLiteral f, T context);

  E transformLetTerm(LetTerm let, T context);

  E transformOverloaded(Overloaded over, T context);

  E transformOverloadedFieldAccess(OverloadedFieldAccess over, T context);

  E transformOverloadVariable(OverloadedVariable var, T context);

  E transformPatternAbstraction(PatternAbstraction pattern, T context);

  E transformRaiseExpression(AbortExpression exp, T context);

  E transformReference(Shriek reference, T context);

  E transformResolved(Resolved res, T context);

  E transformScalar(Scalar scalar, T context);

  E transformTuple(TupleTerm tupleTerm, T context);

  E transformValofExp(ValofExp val, T context);

  E transformVariable(Variable variable, T context);

  E transformVoidExp(VoidExp exp, T context);
}
