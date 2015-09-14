package org.star_lang.star.operators.spawn.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.CafeEnter;

public class NotifyWait
{
  public static class Wait implements IFunction
  {
    public static final String name = "__wait";

    @CafeEnter
    public static void enter(IValue obj) throws EvaluationException
    {

      try {
        obj.wait();
      } catch (InterruptedException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      enter(args[0]);
      return StandardTypes.unit;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.procedureType(tv));
    }
  }

  public static class Notify implements IFunction
  {
    public static final String name = "__notify";

    @CafeEnter
    public static void enter(IValue obj)
    {
      obj.notify();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      enter(args[0]);
      return StandardTypes.unit;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.procedureType(tv));
    }

  }

}
