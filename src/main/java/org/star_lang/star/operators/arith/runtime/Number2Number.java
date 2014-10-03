package org.star_lang.star.operators.arith.runtime;

import java.math.BigDecimal;

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

public abstract class Number2Number
{

  public static class Integer2Integer implements IFunction
  {
    @CafeEnter
    public static int enter(int ix)
    {
      return (int) ix;
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

  public static class Decimal2Integer implements IFunction
  {
    @CafeEnter
    public static int enter(BigDecimal d)
    {
      return d.intValue();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawIntegerType);
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

  public static class Decimal2Long implements IFunction
  {
    @CafeEnter
    public static long enter(BigDecimal d)
    {
      return d.longValue();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawLongType);
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

  public static class Decimal2Float implements IFunction
  {
    @CafeEnter
    public static double enter(BigDecimal d)
    {
      return d.doubleValue();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newFlt(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawFloatType);
    }
  }

  public static class Integer2Decimal implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(int d)
    {
      return new BigDecimal(d);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.intValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawDecimalType);
    }
  }

  public static class Long2Decimal implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(long d)
    {
      return new BigDecimal(d);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.lngValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawDecimalType);
    }
  }

  public static class Float2Decimal implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(double d)
    {
      return new BigDecimal(d);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.fltValue(args[0])));
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawDecimalType);
    }
  }
}
