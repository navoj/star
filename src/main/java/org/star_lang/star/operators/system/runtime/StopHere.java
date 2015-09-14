package org.star_lang.star.operators.system.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.operators.CafeEnter;

public class StopHere implements IFunction
{
  public static final String name = "__stop_here";

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return stop();
  }

  @CafeEnter
  public static IValue stop() throws EvaluationException
  {
    return NTuple.$0Enum;
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
