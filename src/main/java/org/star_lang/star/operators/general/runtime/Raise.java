package org.star_lang.star.operators.general.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.CafeEnter;

public class Raise implements IFunction
{
  public static final String name = "__raise";

  @CafeEnter
  public static IValue enter(EvaluationException msg) throws EvaluationException
  {
    throw msg;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((EvaluationException) args[0]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.functionType(StandardTypes.exceptionType, tv));
  }
}
