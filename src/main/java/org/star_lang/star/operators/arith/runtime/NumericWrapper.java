package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class NumericWrapper
{
  public static class UnwrapBool implements IFunction
  {
    @CafeEnter
    public static boolean enter(IValue src) throws EvaluationException
    {
      return Factory.boolValue(src);
    }

    public static final String UNWRAP_BOOL = "__unwrap_bool";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.booleanType, StandardTypes.rawBoolType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapBool implements IFunction
  {
    @CafeEnter
    public static IValue enter(boolean ix) throws EvaluationException
    {
      return Factory.newBool(ix);
    }

    public static final String WRAP_BOOL = "__wrap_bool";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapBoolean implements IFunction
  {
    @CafeEnter
    public static Boolean enter(IValue src) throws EvaluationException
    {
      return Factory.boolValue(src);
    }

    public static final String UNWRAP_BOOLEAN = "__unwrap_boolean";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.booleanType, StandardTypes.rawBoolType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapBoolean implements IFunction
  {
    @CafeEnter
    public static IValue enter(Boolean ix) throws EvaluationException
    {
      return Factory.newBool(ix);
    }

    public static final String WRAP_BOOLEAN = "__wrap_boolean";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawBoolType, StandardTypes.booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapInt implements IFunction
  {
    @CafeEnter
    public static int enter(IValue src) throws EvaluationException
    {
      return Factory.intValue(src);
    }

    public static final String UNWRAP_INT = "__unwrap_int";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.integerType, StandardTypes.rawIntegerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapInt implements IFunction
  {
    @CafeEnter
    public static IValue enter(int ix) throws EvaluationException
    {
      return Factory.newInt(ix);
    }

    public static final String WRAP_INT = "__wrap_int";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.integerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapInteger implements IFunction
  {
    @CafeEnter
    public static Integer enter(IValue src) throws EvaluationException
    {
      return Factory.integerValue(src);
    }

    public static final String UNWRAP_INTEGER = "__unwrap_integer";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.integerType, StandardTypes.rawIntegerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapInteger implements IFunction
  {
    @CafeEnter
    public static IValue enter(Integer ix) throws EvaluationException
    {
      return Factory.newInteger(ix);
    }

    public static final String WRAP_INTEGER = "__wrap_integer";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.integerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapLng implements IFunction
  {
    @CafeEnter
    public static long enter(IValue src) throws EvaluationException
    {
      return Factory.lngValue(src);
    }

    public static final String UNWRAP_LNG = "__unwrap_lng";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.longType, StandardTypes.rawLongType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapLng implements IFunction
  {
    @CafeEnter
    public static IValue enter(long ix) throws EvaluationException
    {
      return Factory.newLng(ix);
    }

    public static final String WRAP_LNG = "__wrap_lng";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapLong implements IFunction
  {
    @CafeEnter
    public static long enter(IValue src) throws EvaluationException
    {
      return Factory.lngValue(src);
    }

    public static final String UNWRAP_LONG = "__unwrap_long";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.longType, StandardTypes.rawLongType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapLong implements IFunction
  {
    @CafeEnter
    public static IValue enter(Long ix) throws EvaluationException
    {
      return Factory.newLng(ix);
    }

    public static final String WRAP_LONG = "__wrap_long";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapFlt implements IFunction
  {
    @CafeEnter
    public static float enter(IValue src) throws EvaluationException
    {
      return (float) Factory.fltValue(src);
    }

    public static final String UNWRAP_FLT = "__unwrap_flt";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.floatType, StandardTypes.rawFloatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapFlt implements IFunction
  {
    @CafeEnter
    public static IValue enter(float fx) throws EvaluationException
    {
      return Factory.newFlt(fx);
    }

    public static final String WRAP_FLT = "__wrap_flt";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.floatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapFloat implements IFunction
  {
    @CafeEnter
    public static Float enter(IValue src) throws EvaluationException
    {
      return (float) Factory.fltValue(src);
    }

    public static final String UNWRAP_FLOAT = "__unwrap_float";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.floatType, StandardTypes.rawFloatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapFloat implements IFunction
  {
    @CafeEnter
    public static IValue enter(Float fx) throws EvaluationException
    {
      return Factory.newFlt(fx);
    }

    public static final String WRAP_FLOAT = "__wrap_float";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.floatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapDbl implements IFunction
  {
    @CafeEnter
    public static double enter(IValue src) throws EvaluationException
    {
      return Factory.fltValue(src);
    }

    public static final String UNWRAP_DBL = "__unwrap_dbl";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.floatType, StandardTypes.rawFloatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapDbl implements IFunction
  {
    @CafeEnter
    public static IValue enter(double fx) throws EvaluationException
    {
      return Factory.newFlt(fx);
    }

    public static final String WRAP_DBL = "__wrap_dbl";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.floatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class UnwrapDouble implements IFunction
  {
    @CafeEnter
    public static Double enter(IValue src) throws EvaluationException
    {
      return Factory.fltValue(src);
    }

    public static final String UNWRAP_DOUBLE = "__unwrap_double";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.floatType, StandardTypes.rawFloatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class WrapDouble implements IFunction
  {
    @CafeEnter
    public static IValue enter(Double fx) throws EvaluationException
    {
      return Factory.newFlt(fx);
    }

    public static final String WRAP_DOUBLE = "__wrap_double";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.floatType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }
}
