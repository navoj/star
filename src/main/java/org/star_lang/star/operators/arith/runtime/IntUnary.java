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

public abstract class IntUnary
{

  public static class IntAbs implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return Math.abs(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntUMinus implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return -s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntRandom implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      double random = Math.random();
      return new Double(random * s1).intValue();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntSqrt implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.sqrt(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntCbrt implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.cbrt(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntCeil implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntFloor implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntRound implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntLog implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.log(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntLog10 implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.log10(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }

  public static class IntExp implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return (int) Math.exp(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType);
    }
  }
}
