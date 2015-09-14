package org.star_lang.star.operators.binary.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.BinaryWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.data.value.BinaryWrap.BinaryWrapper;
import org.star_lang.star.operators.CafeEnter;

public abstract class BinaryCoercion
{

  public static class Binary2String implements IFunction
  {
    @CafeEnter
    public static IValue enter(BinaryWrap bin) throws EvaluationException
    {
      if (bin instanceof BinaryWrapper) {
        Object content = ((BinaryWrapper) bin).getValue();
        if (content instanceof String)
          return Factory.newString((String) content);
        else
          throw new EvaluationException("not a string");
      } else
        return StringWrap.nonStringEnum;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.binaryType, StandardTypes.stringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((BinaryWrap) args[0]);
    }
  }

  public static class String2Binary implements IFunction
  {
    @CafeEnter
    public static IValue enter(StringWrap str) throws EvaluationException
    {
      if (str == null || str instanceof StringWrap.NonStringWrapper)
        return BinaryWrap.nonBinaryEnum;
      else
        return Factory.newBinary(Factory.stringValue(str));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.binaryType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((StringWrap) args[0]);
    }
  }
}
