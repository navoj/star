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


public class BigNumUnary
{
  public static class DecimalAbs implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      return s1.abs();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalUMinus implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      return s1.negate();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalRandom implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1)
    {
      double random = Math.random();
      return s1.multiply(new BigDecimal(random));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }

  public static class DecimalSqrt implements IFunction
  {
    @CafeEnter
    public static BigDecimal enter(BigDecimal s1) throws EvaluationException
    {
      throw new EvaluationException("sqrt not implemented for decimal numbers");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newDecimal(enter(Factory.decimalValue(args[0])));
    }

    @Override
    public IType getType()
    {
      IType decimalType = StandardTypes.rawDecimalType;
      return TypeUtils.functionType(decimalType, decimalType);
    }
  }
}
