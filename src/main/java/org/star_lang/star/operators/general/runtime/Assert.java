package org.star_lang.star.operators.general.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class Assert implements IFunction
{
  public static final String name = "__assert";

  @CafeEnter
  public static IValue enter(IFunction test, Location where) throws EvaluationException
  {
    assert Factory.boolValue(test.enter()) : "assert failed at " + where.toString();
    return StandardTypes.unit;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((IFunction) args[0], (Location) args[1]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(TypeUtils.functionType(StandardTypes.booleanType), Location.type,
        StandardTypes.unitType);
  }

}
