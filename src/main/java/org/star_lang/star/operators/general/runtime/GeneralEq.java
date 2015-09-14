package org.star_lang.star.operators.general.runtime;

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

public class GeneralEq implements IFunction
{
  public static final String name = "__equal";

  @CafeEnter
  public static IValue enter(IValue lft, IValue rgt) throws EvaluationException
  {
    return Factory.newBool(lft.equals(rgt));
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return Factory.newBool(args[0].equals(args[1]));
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    TypeVar tv = new TypeVar();
    return new UniversalType(tv, TypeUtils.functionType(tv, tv, StandardTypes.booleanType));
  }
}
