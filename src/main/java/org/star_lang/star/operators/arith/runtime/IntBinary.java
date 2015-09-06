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

public abstract class IntBinary
{

  public static class IntPlus implements IFunction
  {
    @CafeEnter
    public static  int enter(int s1, int s2)
    {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMinus implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 - s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntTimes implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 * s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntDivide implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 / s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntRemainder implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 % s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMin implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return Math.min(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntMax implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return Math.max(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntPwr implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return (int) Math.pow(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntLeft implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 << s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }

  public static class IntRight implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 >> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType intType = StandardTypes.rawIntegerType;
      return TypeUtils.functionType(intType, intType, intType);
    }
  }
}
