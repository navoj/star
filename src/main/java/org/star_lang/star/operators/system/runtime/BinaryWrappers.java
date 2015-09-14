package org.star_lang.star.operators.system.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.BinaryWrap.BinaryWrapper;
import org.star_lang.star.operators.CafeEnter;

public class BinaryWrappers
{
  public static class UnwrapBinary implements IFunction
  {
    @CafeEnter
    public static Object enter(IValue src) throws EvaluationException
    {
      if (src instanceof BinaryWrapper)
        return ((BinaryWrapper) src).getValue();
      else
        return null;
    }

    public static final String UNWRAP_BINARY = "__unwrap_binary";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(StandardTypes.binaryType, tv));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapBinary implements IFunction
  {
    @CafeEnter
    public static IValue enter(Object obj) throws EvaluationException
    {
      return Factory.newBinary(obj);
    }

    public static final String WRAP_BINARY = "__wrap_binary";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(tv, StandardTypes.binaryType));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }
}
