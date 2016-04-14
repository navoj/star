package org.star_lang.star.compiler.canonical;

public interface TransformPattern<A, E, P, C, D, T> {
  P transformCastPtn(CastPtn ptn, T context);

  P transformConstructorPtn(ConstructorPtn tuplePtn, T context);

  P transformMatchingPtn(MatchingPattern matches, T context);

  P transformPatternApplication(PatternApplication apply, T context);

  P transformRecordPtn(RecordPtn aggregatePtn, T context);

  P transformRegexpPtn(RegExpPattern ptn, T context);

  P transformScalarPtn(ScalarPtn scalar, T context);

  P transformTuplePtn(TuplePtn tuplePtn, T context);

  P transformVariablePtn(Variable variable, T context);

  P transformWherePattern(WherePattern wherePattern, T context);
}
