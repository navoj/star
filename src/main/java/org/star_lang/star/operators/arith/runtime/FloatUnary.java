package org.star_lang.star.operators.arith.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

/**
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
}
