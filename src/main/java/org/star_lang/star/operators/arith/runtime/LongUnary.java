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
public abstract class LongUnary
{

  public static class LongAbs implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return Math.abs(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongUMinus implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return -s1;
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongRandom implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      double random = Math.random();
      return new Double(random * s1).longValue();
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongSqrt implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return (long) Math.sqrt(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongCbrt implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return (long) Math.cbrt(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongCeil implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return s1;
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongFloor implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return s1;
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongRound implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return s1;
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongLog implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return (long) Math.log(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongLog10 implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return (long) Math.log10(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }

  public static class LongExp implements IFunction
  {
    @CafeEnter
    public static long enter(long s1)
    {
      return (long) Math.exp(s1);
    }

    @Override
    public IType getType()
    {
      IType longType = StandardTypes.rawLongType;
      return TypeUtils.functionType(longType, longType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLng(enter(Factory.lngValue(args[0])));
    }
  }
}
