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
