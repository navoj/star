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
public class FloatTrig
{
  /**
   * Trigonometry functions
   */
  public static class FloatSin implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.sin(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatASin implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.asin(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatSinh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.sinh(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatCos implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.cos(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatACos implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.acos(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatCosh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.cosh(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatTan implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.tan(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatATan implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.atan(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }

  public static class FloatTanh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.tanh(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType floatType = StandardTypes.rawFloatType;
      return TypeUtils.functionType(floatType, floatType);
    }
  }
}
