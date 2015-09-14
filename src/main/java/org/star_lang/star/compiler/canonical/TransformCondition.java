package org.star_lang.star.compiler.canonical;

public interface TransformCondition<A, E, P, C, D, T>
{
  C transformConditionCondition(ConditionCondition conditionCondition, T context);

  C transformConjunction(Conjunction conjunction, T context);

  C transformDisjunction(Disjunction disjunction, T context);

  C transformFalseCondition(FalseCondition falseCondition, T context);

  C transformImplies(Implies implies, T context);

  C transformIsTrue(IsTrue i, T context);

  C transformListSearch(ListSearch ptn, T context);

  C transformMatches(Matches matches, T context);

  C transformNegation(Negation negation, T context);

  C transformOtherwise(Otherwise otherwise, T context);

  C transformSearch(Search predication, T context);

  C transformTrueCondition(TrueCondition trueCondition, T context);
}
