package org.star_lang.star.operators.system.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.CafeEnter;

public class Yield implements IFunction
{
  public static final String name = "__yield";

  @CafeEnter
  public static IValue enter()
  {
    Thread.yield();
    return StandardTypes.unit;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter();
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.procedureType();
  }

}
