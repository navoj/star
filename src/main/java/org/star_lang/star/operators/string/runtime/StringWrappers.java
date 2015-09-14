package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class StringWrappers
{
  public static class String2Raw implements IFunction
  {
    @CafeEnter
    public static String enter(IValue src) throws EvaluationException
    {
      return Factory.stringValue(src);
    }

    public static final String UNWRAP_STRING = "__unwrap_string";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.rawStringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class Raw2String implements IFunction
  {
    @CafeEnter
    public static IValue enter(String str) throws EvaluationException
    {
      return Factory.newString(str);
    }

    public static final String WRAP_STRING = "__wrap_string";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.stringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapChar implements IFunction
  {
    public static final String UNWRAP_CHAR = "__unwrap_char";

    @CafeEnter
    public static int enter(IValue src) throws EvaluationException
    {
      return Factory.charValue(src);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.charType, StandardTypes.rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapChar implements IFunction
  {
    public static final String WRAP_CHAR = "__wrap_char";

    @CafeEnter
    public static IValue enter(int ix) throws EvaluationException
    {
      return Factory.newChar(ix);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawCharType, StandardTypes.charType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapCharacter implements IFunction
  {
    public static final String UNWRAP_CHAR = "__unwrap_character";

    @CafeEnter
    public static Character enter(IValue src) throws EvaluationException
    {
      return (char) Factory.charValue(src);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.charType, StandardTypes.rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapCharacter implements IFunction
  {
    public static final String WRAP_CHAR = "__wrap_character";

    @CafeEnter
    public static IValue enter(Character cx) throws EvaluationException
    {
      return Factory.newChar(cx);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawCharType, StandardTypes.charType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

}
