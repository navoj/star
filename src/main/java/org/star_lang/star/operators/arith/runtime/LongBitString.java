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
 * Bitstring functions
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

public abstract class LongBitString
{

  public static class BitAnd implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 & s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class BitOr implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 | s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class BitXor implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 ^ s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class LongBitNeg implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return ~s1;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }
  }

  public static class BitShl implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 << s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class BitShr implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 >>> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class BitSar implements IFunction
  {
    @CafeEnter
    public static long enter(long s1, long s2)
    {
      return s1 >> s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0]), Factory.lngValue(args[1])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType, longType);
    }
  }

  public static class BitCount implements IFunction
  {
    public static final String name = "__bit_count";
    static final long SK5 = 0x5555555555555555L;
    static final long SK3 = 0x3333333333333333L;
    static final long SKF0 = 0x0F0F0F0F0F0F0F0FL;
    static final long SKFF = 0x00FF00FF00FF00FFL;
    static final long SKFFFF = 0x0000FFFF0000FFFFL;
    static final long SKFFFFFFFF = 0x00000000FFFFFFFFL;

    @CafeEnter
    public static int enter(long bits)
    {
      bits = (bits & SK5) + ((bits >>> 1) & SK5);
      bits = (bits & SK3) + ((bits >>> 2) & SK3);
      bits = (bits & SKF0) + ((bits >>> 4) & SKF0);
      bits = (bits & SKFF) + ((bits >>> 8) & SKFF);
      bits = (bits & SKFFFF) + ((bits >>> 16) & SKFFFF);
      bits = (bits + (bits >>> 32)) & 0x5F;
      return (int) bits;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }
  }

}
