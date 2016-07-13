package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public abstract class Number2Number
{
  public static class Integer2Integer implements IFunction
  {
    @CafeEnter
    public static int enter(int ix)
    {
      return ix;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawIntegerType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return args[0];
    }
  }

  public static class Long2Integer implements IFunction
  {
    @CafeEnter
    public static int enter(long ix)
    {
      return (int) ix;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawIntegerType);
    }
  }

  public static class Float2Integer implements IFunction
  {
    @CafeEnter
    public static int enter(double d)
    {
      return (int) d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawIntegerType);
    }
  }

  public static class Integer2Long implements IFunction
  {
    @CafeEnter
    public static long enter(int d)
    {
      return (long) d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawLongType);
    }
  }

  public static class Float2Long implements IFunction
  {
    @CafeEnter
    public static long enter(double d)
    {
      return (long) d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawLongType);
    }
  }

  public static class Integer2Float implements IFunction
  {
    @CafeEnter
    public static double enter(int d)
    {
      return (double) d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawFloatType);
    }
  }

  public static class Long2Float implements IFunction
  {
    @CafeEnter
    public static double enter(long d)
    {
      return (double) d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawFloatType);
    }
  }
}
