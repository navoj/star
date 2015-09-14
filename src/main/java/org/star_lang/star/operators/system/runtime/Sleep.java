package org.star_lang.star.operators.system.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class Sleep implements IFunction
{
  public static final String name = "sleep";

  @CafeEnter
  public static IValue enter(IValue time) throws EvaluationException
  {
    try {
      Thread.sleep(Factory.lngValue(time));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new EvaluationException("sleep interrupted");
    }
    return StandardTypes.unit;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.procedureType(StandardTypes.longType);
  }
}
