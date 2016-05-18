package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class StringWrappers {
  public static class String2Raw implements IFunction {
    @CafeEnter
    public static String enter(IValue src) throws EvaluationException {
      return Factory.stringValue(src);
    }

    public static final String UNWRAP_STRING = "__unwrap_string";

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.stringType, StandardTypes.rawStringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return args[0];
    }
  }

  public static class Raw2String implements IFunction {
    @CafeEnter
    public static IValue enter(String str) throws EvaluationException {
      return Factory.newString(str);
    }

    public static final String WRAP_STRING = "__wrap_string";

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return TypeUtils.functionType(StandardTypes.rawStringType, StandardTypes.stringType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException {
      return args[0];
    }
  }

}
