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


public class FloatTrig
{
  /**
   * Trigonometry functions
   */
  public static class FloatSin implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.sin(s1);
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

  public static class FloatASin implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.asin(s1);
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

  public static class FloatSinh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.sinh(s1);
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

  public static class FloatCos implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.cos(s1);
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

  public static class FloatACos implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.acos(s1);
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

  public static class FloatCosh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.cosh(s1);
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

  public static class FloatTan implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.tan(s1);
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

  public static class FloatATan implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.atan(s1);
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

  public static class FloatTanh implements IFunction
  {
    @CafeEnter
    public static double enter(double s1)
    {
      return Math.tanh(s1);
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
