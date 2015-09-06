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
 * Bitstring functions for integers
 *
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


public abstract class IntBitString
{

  public static class BitAnd implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 & s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return getBinaryType();
    }
  }

  public static class BitOr implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 | s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return getBinaryType();
    }
  }

  public static class BitXor implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 ^ s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return getBinaryType();
    }
  }

  public static class BitNeg implements IFunction
  {
    @CafeEnter
    public static int enter(int s1)
    {
      return ~s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return getUnaryType();
    }
  }

  public static class BitShl implements IFunction
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
      return getBinaryType();
    }
  }

  public static class BitShr implements IFunction
  {
    @CafeEnter
    public static int enter(int s1, int s2)
    {
      return s1 >>> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0]), Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return getBinaryType();
    }
  }

  public static class BitSar implements IFunction
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
      return getBinaryType();
    }
  }

  public static class BitCount implements IFunction
  {
    public static final String name = "__integer_bit_count";
    static final int SK5 = 0x55555555;
    static final int SK3 = 0x33333333;
    static final int SKF0 = 0x0F0F0F0F;
    static final int SKFF = 0x00FF00FF;
    static final int SKFFFF = 0x0000FFFF;

    @CafeEnter
    public static int enter(int bits)
    {
      bits = (bits & SK5) + ((bits >>> 1) & SK5);
      bits = (bits & SK3) + ((bits >>> 2) & SK3);
      bits = (bits & SKF0) + ((bits >>> 4) & SKF0);
      bits = (bits & SKFF) + ((bits >>> 8) & SKFF);
      bits = (bits & SKFFFF) + ((bits >>> 16) & SKFFFF);
      return bits;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return getUnaryType();
    }
  }

  private static IType getUnaryType()
  {
    IType argType = StandardTypes.rawIntegerType;
    return TypeUtils.functionType(argType, argType);
  }

  private static IType getBinaryType()
  {
    IType argType = StandardTypes.rawIntegerType;
    return TypeUtils.functionType(argType, argType, argType);
  }
}
