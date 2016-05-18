package org.star_lang.star.operators.misc.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class HashCode implements IFunction
{
  public static final String NAME = "__hashCode";

  @CafeEnter
  public static int enter(IValue obj)
  {
    return obj.hashCode();
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return Factory.newInt(enter(args[0]));
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.functionType(tv, StandardTypes.rawIntegerType));
  }
}