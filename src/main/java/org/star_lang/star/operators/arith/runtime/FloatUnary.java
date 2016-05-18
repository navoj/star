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

public abstract class FloatUnary
{

  public static class FloatAbs implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.abs(s1);
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

  public static class FloatUMinus implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return -s1;
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

  public static class FloatRandom implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      double random = Math.random();
      return random * s1;
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

  public static class FloatSqrt implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.sqrt(s1);
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

  public static class FloatCbrt implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.cbrt(s1);
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

  public static class FloatCeil implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.ceil(s1);
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

  public static class FloatFloor implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.floor(s1);
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

  public static class FloatRound implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.round(s1);
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

  public static class FloatLog implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.log(s1);
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

  public static class FloatLog10 implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.log10(s1);
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

  public static class FloatExp implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.exp(s1);
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

  public static class FloatHash implements IFunction
  {
    public static final String name = "__float_hash";
    @CafeEnter
    public static int enter(double s1)
    {
      return Double.hashCode(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type(){
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawIntegerType);
    }
  }

}
