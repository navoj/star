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
 * Binary arithmetic functions
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

public abstract class FloatBinary
{
  private static final IType rawFloatType = StandardTypes.rawFloatType;

  public static class FloatPlus implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return s1 + s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatMinus implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      double d = s1 - s2;
      return d;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatTimes implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return s1 * s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatDivide implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return s1 / s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatRemainder implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return s1 % s2;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatMin implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return Math.min(s1, s2);
    }

    public static final String name = "__float_min";

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatMax implements IFunction
  {
    public static final String name = "__float_max";

    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return Math.max(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class FloatPwr implements IFunction
  {
    @CafeEnter
    public static double enter(double s1, double s2)
    {
      return Math.pow(s1, s2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.fltValue(args[0]), Factory.fltValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawFloatType, rawFloatType, rawFloatType);
    }
  }

  public static class Float2Bits implements IFunction
  {
    public static final String name = "__float_bits";

    @CafeEnter
    public static long enter(double s1)
    {
      return Double.doubleToLongBits(s1);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawFloatType, StandardTypes.rawLongType);
    }
  }

  public static class Bits2Float implements IFunction
  {
    public static final String name = "__bits_float";

    @CafeEnter
    public static double enter(long bits)
    {
      return Double.longBitsToDouble(bits);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFloat(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, rawFloatType);
    }
  }
}
